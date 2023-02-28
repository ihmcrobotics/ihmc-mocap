package optiTrack.ros;

import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;
import us.ihmc.communication.ROS2Tools;
import us.ihmc.communication.ros2.ROS2Helper;
import us.ihmc.euclid.geometry.Pose3D;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2Topic;

import java.util.ArrayList;

public class MocapROS2RigidBodiesPublisher implements MocapRigidbodiesListener
{
	private static final int RIGID_BODY_ID_TO_TRACK = 27;

	private ROS2Topic<Pose3D> mocapTopic;

	private ROS2Node ros2Node;
	private ROS2Helper ros2Helper;
	private final Pose3D poseToPublish = new Pose3D();
//	private final IHMCRealtimeROS2Publisher<RigidBodyTransformMessage> rigidBodyTransformPublisher;

	private final RigidBodyTransform rigidBodyTransform = new RigidBodyTransform();

	int rigidBodyIdToTrack = 1;

	public MocapROS2RigidBodiesPublisher(int interfaceId, ROS2Topic<Pose3D> topic)
	{
		mocapTopic = topic;
		ros2Node = ROS2Tools.createROS2Node(DomainFactory.PubSubImplementation.FAST_RTPS, "mocap_node");
		ros2Helper = new ROS2Helper(ros2Node);

//		ros2Node = ROS2Tools.createRealtimeROS2Node(DomainFactory.PubSubImplementation.FAST_RTPS, StringTools.titleToSnakeCase("mocap_node"));

//		rigidBodyTransformPublisher = ROS2Tools.createPublisher(ros2Node, topic, ROS2QosProfile.BEST_EFFORT());
//		LogTools.info("Spinning Realtime ROS 2 node");
//		ros2Node.spin();

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
				poseToPublish.set(rigidBodyTransform);
				ros2Helper.publish(mocapTopic, poseToPublish);

//				LogTools.info("Publishing RigidBody: ID:{} Transform:{}", RIGID_BODY_ID_TO_TRACK, rigidBodyTransform);
			}
		}
	}

	public static void main(String args[])
	{
		int interfaceId = -1;
		MocapROS2RigidBodiesPublisher mocapClientExample = new MocapROS2RigidBodiesPublisher(interfaceId, ROS2Tools.MOCAP_RIGID_BODY);
	}
}
