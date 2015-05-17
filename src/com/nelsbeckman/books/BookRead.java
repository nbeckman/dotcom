package com.nelsbeckman.books;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import java.util.Date;

/**
 * A record of a book that I have read. It has a author, title, description (in html format) 
 * and a date that I added it to the database.
 * 
 * @author nbeckman
 *
 */
@PersistenceCapable
public class BookRead {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
    private Date dateOfEntry;
	
	@Persistent
	private String author;

	@Persistent
	private String title;
	
	@Persistent
	private com.google.appengine.api.datastore.Text htmlDescription;

	public BookRead(Date dateOfEntry, String author, String title,
			com.google.appengine.api.datastore.Text htmlDescription) {
		super();
		this.dateOfEntry = dateOfEntry;
		this.author = author;
		this.title = title;
		this.htmlDescription = htmlDescription;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Date getDateOfEntry() {
		return dateOfEntry;
	}

	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public com.google.appengine.api.datastore.Text getHtmlDescription() {
		return htmlDescription;
	}

	public void setHtmlDescription(com.google.appengine.api.datastore.Text htmlDescription) {
		this.htmlDescription = htmlDescription;
	}
}
