package com.dl.hadoop.util;

/**
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * the stream utils
 * 
 * 
 */
public final class StreamUtils {

	/**
	 * to transfer the source to target by zero copy
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void transfer(File source, OutputStream target) throws FileNotFoundException, IOException {
		transfer(new FileInputStream(source).getChannel(), Channels.newChannel(target));
	}

	/**
	 * to transfer the source to target by zero copy
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void transfer(File source, WritableByteChannel target) throws FileNotFoundException, IOException {
		transfer(new FileInputStream(source).getChannel(), target);
	}

	/**
	 * to transfer the source to target by zero copy
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @throws IOException
	 */
	public static void transfer(FileInputStream source, OutputStream target) throws IOException {
		transfer(source.getChannel(), Channels.newChannel(target));
	}

	/**
	 * to transfer the source to target by zero copy
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @throws IOException
	 */
	public static void transfer(FileInputStream source, WritableByteChannel target) throws IOException {
		transfer(source.getChannel(), target);
	}

	/**
	 * to transfer the source to target by zero copy
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @throws IOException
	 */
	public static void transfer(FileChannel source, WritableByteChannel target) throws IOException {
		source.transferTo(0, source.size(), target);
	}

}

