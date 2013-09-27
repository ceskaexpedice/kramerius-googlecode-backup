package cz.incad.kramerius.rest.api.replication;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import biz.sourcecode.base64Coder.Base64Coder;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.rest.api.exceptions.ActionNotAllowed;
import cz.incad.kramerius.rest.api.replication.exceptions.ObjectNotFound;
import cz.incad.kramerius.security.IsActionAllowed;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.service.ReplicateException;
import cz.incad.kramerius.service.ReplicationService;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.replication.FormatType;
import cz.incad.kramerius.utils.XMLUtils;


/**
 * CDK replication resource
 * @author pavels
 */
@Path("/cdk")
public class CDKReplicationsResource {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
    @Inject
    ReplicationService replicationService;

    @Inject
    ResourceBundleService resourceBundleService;
    
    @Inject
    Provider<Locale> localesProvider;
    
    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    
    @Inject
    SolrAccess solrAccess;

    @Inject
    IsActionAllowed isActionAllowed;

    @Inject
    Provider<HttpServletRequest> requestProvider;

    @Inject
    Provider<User> userProvider;
    
    
    

    @GET
    @Path("prepare")
    @Produces(MediaType.APPLICATION_XML+";charset=utf-8")
    public Response prepare(@QueryParam("date")String date, @QueryParam("offset") @DefaultValue("100")String offset) throws ReplicateException, UnsupportedEncodingException {
        try {
    		if (date == null) {
    			date = FORMAT.format(new Date());
    		}
        	//TODO: permissions
        	Document document = this.solrAccess.request("fl=PID,modified_date&sort=modified_date%20asc&q=modified_date:{"+date+"%20TO%20NOW}&start=0&rows="+offset);
            return Response.ok().entity(document).build();
        } catch(FileNotFoundException e) {
            throw new ReplicateException(e);
        } catch (IOException e) {
            throw new ReplicateException(e);
        }
    }


    @GET
    @Path("prepare")
    @Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
    public Response prepareJSON(@QueryParam("date")String date, @QueryParam("offset") @DefaultValue("100")String offset) throws ReplicateException, UnsupportedEncodingException {
        try {
    		if (date == null) {
    			date = FORMAT.format(new Date());
    		}
        	//TODO: permissions
        	Document document = this.solrAccess.request("fl=PID,modified_date&sort=modified_date%20asc&q=modified_date:{"+date+"%20TO%20NOW}&start=0&rows="+offset+"&wt=json");
            return Response.ok().entity(document).build();
        } catch(FileNotFoundException e) {
            throw new ReplicateException(e);
        } catch (IOException e) {
            throw new ReplicateException(e);
        }
    }

    /**
     * Returns exported FOXML in xml format
     * @param pid PID of object 
     * @return FOXML as application xml
     * @throws ReplicateException An error has been occured
     * @throws UnsupportedEncodingException  UTF-8 is not supported
     */
    @GET
    @Path("{pid}/foxml")
    @Produces(MediaType.APPLICATION_XML+";charset=utf-8")
    public Response getExportedFOXML(@PathParam("pid")String pid) throws ReplicateException, UnsupportedEncodingException {
        try {
        	//TODO: permissions
            // musi se vejit do pameti
            byte[] bytes = replicationService.getExportedFOXML(pid, FormatType.CDK);
            return Response.ok().entity(XMLUtils.parseDocument(new ByteArrayInputStream(bytes), true)).build();
        } catch(FileNotFoundException e) {
            throw new ObjectNotFound("cannot find pid '"+pid+"'");
        } catch (IOException e) {
            throw new ReplicateException(e);
        } catch (ParserConfigurationException e) {
            throw new ReplicateException(e);
        } catch (SAXException e) {
            throw new ReplicateException(e);
        }
    }

    
    @GET
    @Path("{pid}/foxml")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExportedJSONFOXML(@PathParam("pid")String pid) throws ReplicateException, UnsupportedEncodingException {
        try {
            // musi se vejit do pameti
            byte[] bytes = replicationService.getExportedFOXML(pid, FormatType.CDK);
            char[] encoded = Base64Coder.encode(bytes);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("raw", new String(encoded));
            return Response.ok().entity(jsonObj).build();

        } catch(FileNotFoundException e) {
            throw new ObjectNotFound("cannot find pid '"+pid+"'");
        } catch (IOException e) {
            throw new ReplicateException(e);
        }
    }

}
