package com.openalpr.jni;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.openalpr.jni.json.JSONException;

public class Alpr {	
	private static final Logger _logger = Logger.getLogger("com.openalpr.jni.Alpr");

    static {
    	// Load the OpenALPR library at runtime
		// openalprjni.dll (Windows) or libopenalprjni.so (Linux/Mac)
    	try {
    		System.loadLibrary("openalprjni");
    	}
    	catch (Exception e) {
    		_logger.log(Level.WARNING, "Could not load OpenALPR library.");
    	}
    }
    
    private long _alprPtr;

    private native long initialize(String country, String configFile, String runtimeDir);
    private native void dispose(long alprPtr);

    private native String native_recognize(long alprPtr, String imageFile);
    private native String native_recognize(long alprPtr, byte[] imageBytes);
    private native String native_recognize(long alprPtr, long imageData, int bytesPerPixel, int imgWidth, int imgHeight);

    private native void set_default_region(long alprPtr, String region);
    private native void detect_region(long alprPtr, boolean detectRegion);
    private native void set_top_n(long alprPtr, int topN);
    private native String get_version(long alprPtr);



    public Alpr(String country, String configFile, String runtimeDir)
    {
        _alprPtr = initialize(country, configFile, runtimeDir);
    }

    public void unload()
    {
        dispose(_alprPtr);
    }

    public AlprResults recognize(String imageFile) throws AlprException
    {
        try {
            String json = native_recognize(_alprPtr, imageFile);
            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("Unable to parse ALPR results");
        }
    }


    public AlprResults recognize(byte[] imageBytes) throws AlprException
    {
        try {
            String json = native_recognize(_alprPtr, imageBytes);
            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("Unable to parse ALPR results");
        }
    }


    public AlprResults recognize(long imageData, int bytesPerPixel, int imgWidth, int imgHeight) throws AlprException
    {
        try {
            String json = native_recognize(_alprPtr, imageData, bytesPerPixel, imgWidth, imgHeight);
            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("Unable to parse ALPR results");
        }
    }


    public void setTopN(int topN)
    {
        set_top_n(_alprPtr, topN);
    }

    public void setDefaultRegion(String region)
    {
        set_default_region(_alprPtr, region);
    }

    public void setDetectRegion(boolean detectRegion)
    {
        detect_region(_alprPtr, detectRegion);
    }

    public String getVersion()
    {
        return get_version(_alprPtr);
    }
}
