package com.nelsbeckman.books;


import java.io.IOException;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * The servlet that is run after a user submits a
 * beer event record. Responsible for entering the
 * record into the database.
 * 
 * @author nbeckman
 *
 */
public class AddBookServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		// We don't do any authentication checking here any more because it's
		// now handled by the AppEngine framework itself which does web.xml
		// checking for admin.

		// Persist it
		PersistenceManager pm = PMF.get().getPersistenceManager();
		BookRead book = new BookRead(new Date(),
				req.getParameter("author"),
				req.getParameter("title"),
				new com.google.appengine.api.datastore.Text(req.getParameter("description")));
		try {
			pm.makePersistent(book);
		} finally {
			pm.close();
		}
		// Back to front page
		resp.sendRedirect("/books.jsp");
	}
}
