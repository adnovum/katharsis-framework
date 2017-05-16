package io.katharsis.spring;

import java.io.IOException;
import javax.annotation.Priority;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.katharsis.core.internal.boot.KatharsisBoot;
import io.katharsis.module.http.HttpRequestDispatcher;
import io.katharsis.servlet.internal.ServletRequestContext;

@Priority(20)
public class SpringKatharsisFilter implements Filter {

	private KatharsisBoot boot;

	private FilterConfig filterConfig;

	public SpringKatharsisFilter(KatharsisBoot boot) {
		this.boot = boot;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
			ServletContext servletContext = filterConfig.getServletContext();
			HttpRequestDispatcher requestDispatcher = boot.getRequestDispatcher();
			ServletRequestContext context = new ServletRequestContext(servletContext, (HttpServletRequest) req,
					(HttpServletResponse) res, boot.getWebPathPrefix());
			requestDispatcher.process(context);
			if (!context.checkAbort()) {
				chain.doFilter(req, res);
			}
		}
		else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {

	}
}

