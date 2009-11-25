package org.jclouds.rimuhosting.miro.data;

/**
 * Do as much validation as possible to save http requests. No need to go overboard though.
 */
public interface PostData {
	public void validate();
}
