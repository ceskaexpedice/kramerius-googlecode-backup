package cz.incad.kramerius.service.impl;

import cz.incad.kramerius.intconfig.InternalConfiguration;
import cz.incad.kramerius.processes.impl.ProcessStarter;
import cz.incad.kramerius.processes.utils.ProcessUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class IndexerProcessStarter {
	
	private static final Logger log = Logger.getLogger(IndexerProcessStarter.class.getName());


	public static void spawnIndexer(String title, String uuid) {
		log.info("Spawn indexer: title: "+title+" uuid: "+uuid);
		String base = ProcessUtils.getLrServlet();    
	    if (base == null || uuid == null){
	    	log.severe("Cannot start indexer, invalid arguments: base:"+base+" uuid:"+uuid);
	        return;
	    }
	    if (title == null || "".equals(title.trim())){
	    	title = "untitled";
	    }
	    title = title.replaceAll(",", " ");
	    String url = null;
		try {
			url = base + "?action=start&def=reindex&out=text&params=fromKrameriusModel,"+uuid+","+URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.severe(e1.getMessage());
		}
	    try {
	        ProcessStarter.httpGet(url);
	    } catch (Exception e) {
	        log.severe("Error spawning indexer for "+uuid+":"+e);
	    }
	}

	public static void spawnIndexRemover(String pid_path, String uuid) {
		log.info("spawnIndexRemower: pid_path: "+pid_path+" uuid: "+uuid);
		String base = ProcessUtils.getLrServlet();
		if (base == null || pid_path == null || uuid == null){
	    	log.severe("Cannot start indexer, invalid arguments: base:"+base+" uuid:"+uuid+" pid_path:"+pid_path);
	        return;
	    }
	    if (pid_path.endsWith("/")){
	    	pid_path = pid_path.substring(0,pid_path.length()-1);
	    }
	    String url = base +"?action=start&def=reindex&out=text&params=deleteDocument,"+pid_path+","+uuid;
	    try {
	        ProcessStarter.httpGet(url);
	    } catch (Exception e) {
	        log.severe("Error spawning indexer for "+uuid+":"+e);
	    }
	}
}


