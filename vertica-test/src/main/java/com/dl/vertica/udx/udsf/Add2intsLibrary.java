package com.dl.vertica.udx.udsf;

import com.vertica.sdk.UDXLibrary;

public class Add2intsLibrary extends UDXLibrary{
	@Override public String getAuthor() {return "Whizzo Analytics Ltd.";}
	@Override public String getLibraryBuildTag() {return "1234";}
	@Override public String getLibraryVersion() {return "1.0";}
	@Override public String getLibrarySDKVersion() {return "7.0.0";}
	@Override public String getSourceUrl() {
		return "http://example.com/add2ints";
	}
	@Override public String getDescription() {
		return "My Awesome Add 2 Ints Library";
	}
	@Override public String getLicensesRequired() {return "";}
	@Override public String getSignature() {return "";}
}
