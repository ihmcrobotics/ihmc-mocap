package optiTrack;

import us.ihmc.euclid.tuple3D.Vector3D;

public class MocapMarker
{
	private int markerId;
	private int rigidBodyId;
	private Vector3D position;
	private float markerSize;
	private boolean markerIsOccludedInThisFrame;
	private boolean positionSolvedFromPointCloud;
	private boolean positionSolvedFromModel;
	private boolean markerIsAssociated;
	private boolean markerIsUnLabeledWithId;
	private boolean markerHasActiveLEDMarker;
	private float residualError;
	private boolean labeled = false;

	public MocapMarker(int id, Vector3D position, float markerSize)
	{
		this.markerId = id;
		this.position = position;
		this.markerSize = markerSize;
		this.labeled = false;
	}

	public MocapMarker(int id, int rigidBodyId, Vector3D position, float markerSize,
	        boolean markerIsOccludedInThisFrame, boolean positionSolvedFromPointCloud,
	        boolean positionSolvedFromModel, boolean markerIsAssociated,
	        boolean markerIsUnLabeledWithId, boolean markerHasActiveLEDMarker, float residualError)
	{
		this.labeled = true;
		this.markerId = id;
		this.rigidBodyId = rigidBodyId;
		this.position = position;
		this.markerSize = markerSize;
		this.markerIsOccludedInThisFrame = markerIsOccludedInThisFrame;
		this.positionSolvedFromPointCloud = positionSolvedFromPointCloud;
		this.positionSolvedFromModel = positionSolvedFromModel;
		this.markerIsAssociated = markerIsAssociated;
		this.markerIsUnLabeledWithId = markerIsUnLabeledWithId;
		this.markerHasActiveLEDMarker = markerHasActiveLEDMarker;
		this.residualError = residualError;
	}

	public int getId()
	{
		return markerId;
	}

	public Vector3D getPosition()
	{
		return position;
	}

	public float getMarkerSize()
	{
		return markerSize;
	}

	public int getRigidBodyId()
	{
		return rigidBodyId;
	}

	public void setRigidBodyId(int rigidBodyId)
	{
		this.rigidBodyId = rigidBodyId;
	}

	public boolean isMarkerIsOccludedInThisFrame()
	{
		return markerIsOccludedInThisFrame;
	}

	public void setMarkerIsOccludedInThisFrame(boolean markerIsOccludedInThisFrame)
	{
		this.markerIsOccludedInThisFrame = markerIsOccludedInThisFrame;
	}

	public boolean isPositionSolvedFromPointCloud()
	{
		return positionSolvedFromPointCloud;
	}

	public void setPositionSolvedFromPointCloud(boolean positionSolvedFromPointCloud)
	{
		this.positionSolvedFromPointCloud = positionSolvedFromPointCloud;
	}

	public boolean isPositionSolvedFromModel()
	{
		return positionSolvedFromModel;
	}

	public void setPositionSolvedFromModel(boolean positionSolvedFromModel)
	{
		this.positionSolvedFromModel = positionSolvedFromModel;
	}

	public boolean isMarkerIsAssociated()
	{
		return markerIsAssociated;
	}

	public void setMarkerIsAssociated(boolean markerIsAssociated)
	{
		this.markerIsAssociated = markerIsAssociated;
	}

	public boolean isMarkerIsUnLabeledWithId()
	{
		return markerIsUnLabeledWithId;
	}

	public void setMarkerIsUnLabeledWithId(boolean markerIsUnLabeledWithId)
	{
		this.markerIsUnLabeledWithId = markerIsUnLabeledWithId;
	}

	public boolean isMarkerHasActiveLEDMarker()
	{
		return markerHasActiveLEDMarker;
	}

	public void setMarkerHasActiveLEDMarker(boolean markerHasActiveLEDMarker)
	{
		this.markerHasActiveLEDMarker = markerHasActiveLEDMarker;
	}

	public float getResidualError()
	{
		return residualError;
	}

	public void setResidualError(float residualError)
	{
		this.residualError = residualError;
	}

	public String toString()
	{
		String message = "\n";

		if (labeled)
		{
			message = message + "Marker ID: " + getId();
			message = message + "\nRigid Body ID: " + getRigidBodyId();
			message = message + "\nMarker Size: " + getMarkerSize();
			message = message + "\nResidual Error: " + getResidualError();
			message = message + "\nIs Occluded: " + isMarkerIsOccludedInThisFrame();
			message = message + "\nIs Associated: " + isMarkerIsAssociated();
			message = message + "\nIs UnLabeled With Id: " + isMarkerIsUnLabeledWithId();
			message = message + "\nIs Solved From Model: " + isPositionSolvedFromModel();
			message = message + "\nIs Solved From Point Cloud: " + isPositionSolvedFromPointCloud();
			message = message + "\nHas Active LED: " + isMarkerHasActiveLEDMarker();
			message = message + "\nPosition: " + getPosition();
		}
		else
		{
			message = message + "Marker ID: " + getId();
			message = message + "\nPosition: " + getPosition();
		}

		return message;
	}
}
