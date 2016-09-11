package com.teamwizardry.refraction.api;

/**
 * Created by Saad on 9/11/2016.
 */
public class GraphPointObject {
	public int x, y;
	public ColorType colorType;
	public GraphPointObject(int x, int y, ColorType type) {}

	public enum ColorType {
		R, G, B, A
	}
}
