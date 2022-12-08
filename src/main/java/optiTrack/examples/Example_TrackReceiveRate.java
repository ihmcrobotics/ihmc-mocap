package optiTrack.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;

import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;

public class Example_TrackReceiveRate implements MocapRigidbodiesListener
{
	private MocapDataClient mocapClient;
	private DecimalFormat dc = new DecimalFormat("#.00");
	private long lastTimeReceived = 0;

	public Example_TrackReceiveRate(int interfaceId)
	{
		mocapClient = new MocapDataClient(interfaceId);
		mocapClient.registerRigidBodiesListener(this);
	}

	public static void main(String args[])
	{
		int interfaceId = -1;

		if (args.length > 0)
		{
			interfaceId = Integer.parseInt(args[0]);
		}

		if (interfaceId == 0)
			interfaceId = -1;

		new Example_TrackReceiveRate(interfaceId);
	}

	@Override
	public void updateRigidbodies(ArrayList<MocapRigidBody> listOfRigidbodies)
	{

		if (System.currentTimeMillis() - lastTimeReceived > 1000)
		{
			lastTimeReceived = System.currentTimeMillis();
			System.out.println("Received " + listOfRigidbodies.size() + " rigid bodies");
			System.out.println("Receiving at: "
			        + dc.format(mocapClient.getMocapDataReceivingFrequency()) + " Hz");
		}
	}
}
