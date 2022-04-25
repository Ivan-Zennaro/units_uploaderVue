package org.example.rest.filters;

import javax.servlet.annotation.WebFilter;
import com.googlecode.objectify.ObjectifyFilter;

@WebFilter(urlPatterns = {"/*"})
public class OfyFilter extends ObjectifyFilter {}
