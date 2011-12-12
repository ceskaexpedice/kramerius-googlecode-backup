package cz.incad.kramerius.service.impl;

import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.service.DeleteService;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class DeleteServiceImpl implements DeleteService {

    
    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    @Inject
    KConfiguration configuration;

    
    private static final String INFO = "info:fedora/";
    
    @Override
    public void deleteTree(String pid, String message) {
        Set<String> pids = fedoraAccess.getPids(pid);
        for (String s : pids) {
        	String p = s.replace(INFO, "");
            fedoraAccess.getAPIM().purgeObject(p, message, false);
        }
    }
    
    /**
     * args[0] uuid of the root object (without uuid: prefix)
     * args[1] pid_path to root object
     */
    public static void main(String[] args){
        DeleteServiceImpl inst = new DeleteServiceImpl();
        inst.fedoraAccess = new FedoraAccessImpl(null);
        inst.deleteTree("uuid:"+args[0], null);
        IndexerProcessStarter.spawnIndexRemover(args[1], args[0]);
    }

}