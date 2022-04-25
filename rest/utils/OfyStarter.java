package org.example.rest.utils;

import com.googlecode.objectify.ObjectifyService;
import org.example.rest.entities.storage.Actor;
import org.example.rest.entities.storage.File;
import org.example.rest.persistence.ActorHelper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener()
public class OfyStarter implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {
        public OfyStarter() {}
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.register(Actor.class);
        ObjectifyService.register(File.class);

    }
    public void contextDestroyed(ServletContextEvent sce) {}
    public void sessionCreated(HttpSessionEvent se) {
        Actor admin = new Actor("admin@admin.it","admin@gmail.com","admin", Md5.getMd5("password"), Roles.ADMIN);
        ActorHelper.saveDelayed(admin);
    }
    public void sessionDestroyed(HttpSessionEvent se) { }
    public void attributeAdded(HttpSessionBindingEvent sbe) {}
    public void attributeRemoved(HttpSessionBindingEvent sbe) {}
    public void attributeReplaced(HttpSessionBindingEvent sbe) {}
}
