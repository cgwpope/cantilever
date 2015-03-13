package com.google.code.cantilever;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import davmail.AbstractServer;
import davmail.Settings;
import davmail.caldav.CaldavServer;
import davmail.exception.DavMailException;
import davmail.http.DavGatewayHttpClientFacade;
import davmail.imap.ImapServer;
import davmail.smtp.SmtpServer;

public class CantileverService extends Service {

    private static final ArrayList<AbstractServer> SERVER_LIST = new ArrayList<AbstractServer>();

	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//nothing to be read from here initially
		//so just do onormal startup stuff - apply settings and kick off server threads
        Settings.load();
        
        
        
        // register custom SSL Socket factory
//        DavGatewaySSLProtocolSocketFactory.register();

        // prepare HTTP connection pool
        DavGatewayHttpClientFacade.start();

        SERVER_LIST.clear();

        int smtpPort = Settings.getIntProperty("davmail.smtpPort");
        if (smtpPort != 0) {
            SERVER_LIST.add(new SmtpServer(smtpPort));
        }
//        int popPort = Settings.getIntProperty("davmail.popPort");
//        if (popPort != 0) {
//            SERVER_LIST.add(new PopServer(popPort));
//        }
        int imapPort = Settings.getIntProperty("davmail.imapPort");
        if (imapPort != 0) {
            SERVER_LIST.add(new ImapServer(imapPort));
        }
        
        int caldavPort = Settings.getIntProperty("davmail.caldavPort");
        if (caldavPort != 0) {
            SERVER_LIST.add(new CaldavServer(caldavPort));
        }

//XXX - LDAP Server        
//        int ldapPort = Settings.getIntProperty("davmail.ldapPort");
//        if (ldapPort != 0) {
//            SERVER_LIST.add(new LdapServer(ldapPort));
//        }

        for (AbstractServer server : SERVER_LIST) {
            try {
                server.bind();
                server.start();
                Log.d(getClass().getName(), "LOG_PROTOCOL_PORT: " +  server.getProtocolName() + ":" +   server.getPort());

            } catch (DavMailException e) {
                Log.e(getClass().getName(), e.getBundleMessage().format(), e);
            } 
        }

        
        return START_STICKY;
	}
	
	@Override
	public boolean stopService(Intent name) {
		for (AbstractServer svc : SERVER_LIST) {
			svc.close();
		}
		
		return true;
	}

}
