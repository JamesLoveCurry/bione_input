package com.yusys.bione.plugin.spreadjs.htz.core;

public class RGB {
	
	public RGB(int r, int g, int b)
    {
        red = r;
        green = g;
        blue = b;
    }

    public int getRed()
    {
        return red;
    }

    public int getGreen()
    {
        return green;
    }

    public int getBlue()
    {
        return blue;
    }

    private static int red;
    private static int green;
    private static int blue;
    private static RGB rgb;
    
    public static  RGB getDefaultRGB()
    {
    	rgb = new RGB(red,green,blue);
    	return rgb;
    }
    
}
