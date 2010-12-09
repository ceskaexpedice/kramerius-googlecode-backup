/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.kramerius.resourceindex;

import java.util.ArrayList;
import org.w3c.dom.Document;

/**
 *
 * @author Alberto
 */
public interface IResourceIndex { 

    public Document getFedoraObjectsFromModelExt(String model, int limit, int offset, String orderby, String orderDir) throws Exception;
    public ArrayList<String> getFedoraPidsFromModel(String model, int limit, int offset) throws Exception;

}
