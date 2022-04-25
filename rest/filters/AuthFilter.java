package org.example.rest.filters;

import org.example.rest.entities.storage.Actor;
import org.example.rest.persistence.ActorHelper;
import org.example.rest.utils.JToken;
import org.example.rest.utils.Roles;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "AuthFilter",urlPatterns = {"/api/cons/*","/api/admin/*","/api/upl/*"})
public class AuthFilter implements Filter {
    public void destroy() {}

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {

        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest re = (HttpServletRequest) req;
        String url = re.getRequestURL().toString();
        String token = re.getHeader("Authorization");

        try {
            if (token != null && !token.equals("") && !token.equals("null") && token.startsWith("Bearer ")){
                token = token.substring(7);
                Actor actor = ActorHelper.getActorByToken(token);
                if (actor != null && JToken.verifyToken(token) && checkPrivileges(actor, url))
                    chain.doFilter(req, resp);
                else response.getWriter().print("CHANGE TOKEN");
            }
            else  response.getWriter().print("NO TOKEN");
        }
         catch (Exception e){e.printStackTrace();}
    }
    public void init(FilterConfig config){}

    //controlla se utente ha permesso di interagire con url richiesto
    public boolean checkPrivileges (Actor actor, String url) {
        if (actor == null)
            return false;
        if (url.contains("/upl/") && actor.getRole().equals(Roles.CONSUMER))
            return false;
        if (url.contains("/admin/") && (actor.getRole().equals(Roles.CONSUMER) || actor.getRole().equals(Roles.UPLOADER) ))
            return false;
        return true;
    }
}
