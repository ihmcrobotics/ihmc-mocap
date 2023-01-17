package optiTrack.ros;

import controller_msgs.msg.dds.RigidBodyTransformMessage;
import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;
import us.ihmc.communication.IHMCRealtimeROS2Publisher;
import us.ihmc.communication.ROS2Tools;
import us.ihmc.communication.packets.MessageTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.log.LogTools;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.ros2.ROS2QosProfile;
import us.ihmc.ros2.ROS2Topic;
import us.ihmc.ros2.RealtimeROS2Node;
import us.ihmc.tools.string.StringTools;

import java.util.ArrayList;

public class ROS2RigidBodiesPublisher implements MocapRigidbodiesListener
{
	private static final int RIGID_BODY_ID_TO_TRACK = 23;

	private RealtimeROS2Node ros2Node;
	private final RigidBodyTransformMessage rigidBodyTransformMessage = new RigidBodyTransformMessage();
	private final IHMCRealtimeROS2Publisher<RigidBodyTransformMessage> rigidBodyTransformPublisher;

	private final RigidBodyTransform rigidBodyTransform = new RigidBodyTransform();

	int rigidBodyIdToTrack = 1;

	public ROS2RigidBodiesPublisher(int interfaceId, ROS2Topic<RigidBodyTransformMessage> topic)
	{
		ros2Node = ROS2Tools.createRealtimeROS2Node(DomainFactory.PubSubImplementation.FAST_RTPS, StringTools.titleToSnakeCase("mocap_node"));

		rigidBodyTransformPublisher = ROS2Tools.createPublisher(ros2Node, topic, ROS2QosProfile.BEST_EFFORT());
		LogTools.info("Spinning Realtime ROS 2 node");
		ros2Node.spin();

		MocapDataClient mocapClient = new MocapDataClient(interfaceId);
		mocapClient.registerRigidBodiesListener(this);
	}

	public int getTrackingId()
	{
		return rigidBodyIdToTrack;
	}

	@Override
	public void updateRigidbodies(ArrayList<MocapRigidBody> listOfRigidbodies)
	{
		for (MocapRigidBody rb : listOfRigidbodies)
		{
			if(rb.getId() == RIGID_BODY_ID_TO_TRACK)
			{
				rigidBodyTransform.set(rb.getOrientation(), rb.getPosition());
				MessageTools.toMessage(rigidBodyTransform, rigidBodyTransformMessage);
				rigidBodyTransformPublisher.publish(rigidBodyTransformMessage);

//				LogTools.info("Publishing RigidBody: ID:{} Transform:{}", RIGID_BODY_ID_TO_TRACK, rigidBodyTransform);
			}
		}
	}

	public static void main(String args[])
	{
		int interfaceId = -1;
		ROS2RigidBodiesPublisher mocapClientExample = new ROS2RigidBodiesPublisher(interfaceId, ROS2Tools.MOCAP_RIGID_BODY);
	}
}
