package cg.render.materials;

import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.*;
import cg.render.assets.Texture;

/**
 * Created by Hobbit on 5/7/16.
 */
public class ReflectiveMaterial extends Material {

	//New fields:
	private final Color reflectivityColor;
	private final Texture reflectivityColorTexture;
	private final Vec2 reflectivityColorTextureOffset;
	private final Vec2 reflectivityColorTextureScale;
	
	public ReflectiveMaterial(Channel reflectivityColorChannel) {
		this.reflectivityColor = reflectivityColorChannel.colorComponent;
		if (reflectivityColorChannel.isTextured()) {
			this.reflectivityColorTexture = reflectivityColorChannel.getTexture();
			this.reflectivityColorTextureOffset = reflectivityColorChannel.textureOffset;
			this.reflectivityColorTextureScale = reflectivityColorChannel.textureScale;
		} else {
			this.reflectivityColorTexture = null;
			this.reflectivityColorTextureOffset = null;
			this.reflectivityColorTextureScale = null;			
		}
	}
	
    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        if (col.getRay().getHops() > scene.getReflectionTraceDepth()) {
           return Scene.BACKGROUND_COLOR;
        }

        Vec3 d = col.getRay().getDirection().mul(-1);
        Vec3 reflection = d.reflect(col.getNormal());

		Color reflectiveTexColor = getColor(col.u, col.v);

		Ray reflectionRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.00001)), reflection, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
        QuickCollision qc =  scene.collideRay(reflectionRay);
        if (qc != null) {
            Collision reflectionCol = qc.completeCollision();
            Color reflectionColor = reflectionCol.getPrimitive().getMaterial().getSurfaceColor(reflectionCol, scene);
            return reflectiveTexColor.mul(reflectionColor);
        }

        return Scene.BACKGROUND_COLOR;
    }

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
		if (col.getRay().getHops() > scene.getMaxTraceDepth()) {
			return new PathData(Color.BLACK);
		}

		Vec3 d = col.getRay().getDirection().mul(-1);
		Vec3 reflection = d.reflect(col.getNormal());

		Color reflectiveTexColor = getColor(col.u, col.v);

		Ray reflectionRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.00001)), reflection, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
		QuickCollision qc =  scene.collideRay(reflectionRay);
		if (qc != null) {
			Collision reflectionCol = qc.completeCollision();
			PathData pd = reflectionCol.getPrimitive().getMaterial().traceSurfaceColor(reflectionCol, scene);
			pd.color = reflectiveTexColor.mul(pd.color);
			return pd;
		}

		return new PathData(Color.BLACK);
	}

	public Color getColor(double u, double v) {
		Color reflectiveTexColor = reflectivityColor;
		if (reflectivityColorTexture != null) {
			Color texCol = reflectivityColorTexture.getOffsetScaledSample(reflectivityColorTextureOffset, reflectivityColorTextureScale, u, v);
			reflectiveTexColor = reflectiveTexColor.mul(texCol);
		}

		return reflectiveTexColor;
	}
}
