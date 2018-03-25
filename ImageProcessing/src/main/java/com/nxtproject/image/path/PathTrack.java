package com.nxtproject.image.path;

import java.util.ArrayList;

public class PathTrack {
	public static PathTrackResult getAngleofDeviation(ArrayList<Vector2f> verticies, int vertexIndex, Vector2f cartPosition, Vector2f direction) {
		if(vertexIndex >= verticies.size() - 1) return new PathTrackResult(0, vertexIndex);
		
		boolean isLast = vertexIndex == verticies.size() - 2;
		
		Vector2f back = verticies.get(vertexIndex);
		Vector2f front = verticies.get(vertexIndex + 1);
		Vector2f edge = front.sub(back);
		
		Vector2f position = back.add(edge.getProjectionOf(cartPosition.sub(back)));
		
		float progress = position.sub(back).length() / edge.length();
		Vector2f lookAtPos = getLookAtPoint(verticies, vertexIndex, progress, isLast);
		Vector2f calculatedDirection = lookAtPos.sub(cartPosition).normalized();
		
		if(!isLast) {
			Vector2f afterBack = verticies.get(vertexIndex + 1);
			Vector2f afterEdge = verticies.get(vertexIndex + 2).sub(afterBack);
			Vector2f afterPosition = afterBack.add(afterEdge.getProjectionOf(cartPosition.sub(afterBack)));
			
			if(cartPosition.sub(afterPosition).length() < cartPosition.sub(position).length()) {
				vertexIndex++;
			} else if (progress >= 1) vertexIndex++;
		} else if (progress >= 1) vertexIndex++;
		
		float angle = (float)Math.toDegrees(direction.getAngleBetween(calculatedDirection));
		return new PathTrackResult(angle, vertexIndex);
	}
	
	public static Vector2f getLookAtPoint(ArrayList<Vector2f> verticies, int currentEdge, float progress, boolean isLast) {
		Vector2f back = verticies.get(currentEdge);
		Vector2f front = verticies.get(currentEdge + 1);
		Vector2f edge = front.sub(back);
		
		progress += 0.1f;
		
		if(!isLast && progress > 1) {
			back = verticies.get(currentEdge + 1);
			front = verticies.get(currentEdge + 2);
			edge = front.sub(back);
			
			progress -= 1.0f;
		} else if(isLast) {
			progress = Math.min(1.0f, progress);
		}
		
		return back.add(edge.normalized().mul(edge.length() * progress)); 
	}
	
	public static class PathTrackResult {
		private final float angle;
		private final int vertexIndex;
		
		public PathTrackResult(float agle, int vertexIndex) {
			this.angle = agle;
			this.vertexIndex = vertexIndex;
		}
		
		public float getAngle() {
			return angle;
		}
		
		public int getVertexIndex() {
			return vertexIndex;
		}
	}
}