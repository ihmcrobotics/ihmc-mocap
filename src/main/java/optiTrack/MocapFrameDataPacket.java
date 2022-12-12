package optiTrack;

import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple4D.Quaternion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MocapFrameDataPacket
{
   private static MocapRigidBody rigidBody;

   ;
   private static boolean DEBUG = false;
   public short messageID;
   public short payloadSize;
   public int frameNumber;
   public int numberOfMarkerSets;
   public int numberOfRigidBodies;
   public int rigidBodyId;
   public float posX;
   public float posY;
   public float posZ;
   public float rotX;
   public float rotY;
   public float rotZ;
   public float rotS;
   public int nRigidMarkers;
   public int numberOfUnLabeledMarkers;
   public float uMarkerX;
   public float uMarkerY;
   public float uMarkerZ;

   public Vector3D[] markerPosition;
   public int[] markerIds;
   public float[] markerSizes;

   public int numberOfSkeletons;
   public float latency;
   public int numberOfLabeledMarkers;

   public float meanMarkerError;

   public boolean isTracked = false;

   public int type = 0;

   public int numberOfMarkers;
   public double timestamp;
   private MessageType messageType;
   private ArrayList<MocapRigidBody> listOfRigidbodies;
   private ArrayList<MocapMarkerSet> listofMarkerSets;
   private ArrayList<MocapMarker> listOfUnLabeledMarkers;
   private ArrayList<MocapMarker> listOfLabeledMarkers;
   public MocapFrameDataPacket(byte[] bytes) throws IOException
   {
      createFromBytes(bytes);
   }

   public static float readFloatFromByteArray(byte ba[], int offset)
   {
      int ivalue = readIntFromByteArray(ba, offset);
      //if(debug) System.out.println(Integer.toHexString(ivalue));
      return Float.intBitsToFloat(ivalue);
   }

   public static int readIntFromByteArray(byte ba[], int offset)
   {
      return (((int) ba[offset + 0]) & 0xff) | ((((int) ba[offset + 1]) << 8) & 0xff00) | ((((int) ba[offset + 2]) << 16) & 0xff0000) | (
            (((int) ba[offset + 3]) << 24) & 0xff000000);
   }

   private void createFromBytes(byte[] bytes) throws IOException
   {
      listOfRigidbodies = new ArrayList<MocapRigidBody>();
      listofMarkerSets = new ArrayList<MocapMarkerSet>();
      listOfUnLabeledMarkers = new ArrayList<MocapMarker>();
      listOfLabeledMarkers = new ArrayList<MocapMarker>();

      ByteBuffer buf = ByteBuffer.wrap(bytes);

      buf.order(ByteOrder.LITTLE_ENDIAN);

      messageID = buf.getShort();
      payloadSize = buf.getShort();

      decodeMessageType();

      if (DEBUG)
      {
         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>START MESSAGE");
         System.out.println("MOCAP Info:");
         System.out.println("Mssg ID: " + messageID);
         System.out.println("Mssg Payload Size: " + payloadSize);
      }

      switch (messageType)
      {
         case NAT_FRAME_OF_DATA:
            frameNumber = buf.getInt();
            numberOfMarkerSets = buf.getInt();

            if (DEBUG)
            {
               System.out.println("Message type: FrameData Message");
               System.out.println("Frame #: " + frameNumber);
               System.out.println("# of datasets: " + numberOfMarkerSets);
            }

            parseMarkerSets(buf);

            numberOfUnLabeledMarkers = buf.getInt();
            parseUnLabeledMarkers(buf);

            numberOfRigidBodies = buf.getInt();
            parseRigidBodies(buf);

            numberOfSkeletons = buf.getInt();

            numberOfLabeledMarkers = buf.getInt();
            parseLabeledMarkers(buf);

            int forcePlates = buf.getInt();
            int devices = buf.getInt();

            parseTimeCode(buf);

            if (DEBUG)
            {
               System.out.println("# of Skeletons: " + numberOfSkeletons);
               System.out.println("Labeled Markers: " + numberOfLabeledMarkers);
               System.out.println("Timestamp: " + timestamp);
               System.out.println("Force Plates: " + forcePlates);
               System.out.println("Devices: " + devices);
               System.out.println("<<<<<<<<<<<<<<<<<<<<<<END MESSAGE");
            }
            break;
         case NAT_CONNECT:
            break;
         case NAT_MESSAGE_STRING:
            break;
         case NAT_MODEL_DEF:
            break;
         case NAT_REQUEST:
            break;
         case NAT_REQUEST_FRAME_OF_DATA:
            break;
         case NAT_REQUEST_MODEL_DEF:
            System.out.println("Description Message");
            numberOfMarkerSets = buf.getInt();

            System.out.println("# of datasets: " + numberOfMarkerSets);

            for (int i = 0; i <= numberOfMarkerSets; i++)
            {
               System.out.println("Desc. Type: " + buf.getInt());
            }

            type = buf.getInt();

            System.out.println("Type: " + type);
            break;
         case NAT_RESPONSE:
            break;
         case NAT_SERVER_INFO:
            break;
         case NAT_UNDEFINED:
            break;
         case NAT_UNRECOGNIZED_REQUEST:
            break;
         default:
            break;
      }
   }

   private void decodeMessageType()
   {
      if (messageID == 0)
      {
         messageType = MessageType.NAT_CONNECT;
      }
      else if (messageID == 1)
      {
         messageType = MessageType.NAT_SERVER_INFO;
      }
      else if (messageID == 2)
      {
         messageType = MessageType.NAT_REQUEST;
      }
      else if (messageID == 3)
      {
         messageType = MessageType.NAT_RESPONSE;
      }
      else if (messageID == 4)
      {
         messageType = MessageType.NAT_REQUEST_MODEL_DEF;
      }
      else if (messageID == 5)
      {
         messageType = MessageType.NAT_MODEL_DEF;
      }
      else if (messageID == 6)
      {
         messageType = MessageType.NAT_REQUEST_FRAME_OF_DATA;
      }
      else if (messageID == 7)
      {
         messageType = MessageType.NAT_FRAME_OF_DATA;
      }
      else if (messageID == 8)
      {
         messageType = MessageType.NAT_MESSAGE_STRING;
      }
      else if (messageID == 100)
      {
         messageType = MessageType.NAT_UNRECOGNIZED_REQUEST;
      }
      else
      {
         messageType = MessageType.NAT_UNDEFINED;
      }
   }

   private void parseLabeledMarkers(ByteBuffer bb)
   {
      if (DEBUG)
      {
         System.out.println();
         System.out.println("# of Labeled markers: " + numberOfLabeledMarkers);
      }

      for (int i = 0; i < numberOfLabeledMarkers; i++)
      {
         int id = bb.getInt();

         int rigidBodyId = id >> 16;
         int markerId = id & 0x0000ffff;

         float xPos = bb.getFloat();
         float yPos = bb.getFloat();
         float zPos = bb.getFloat();
         float markerSize = bb.getFloat();
         short properties = bb.getShort();

         boolean markerIsOccludedInThisFrame = (properties & 0x01) != 0;
         boolean positionSolvedFromPointCloud = (properties & 0x02) != 0;
         boolean positionSolvedFromModel = (properties & 0x04) != 0;
         boolean markerIsAssociated = (properties & 0x08) != 0;
         boolean markerIsUnLabeledWithId = (properties & 0x10) != 0;
         boolean markerHasActiveLEDMarker = (properties & 0x20) != 0;

         float residualError = bb.getFloat();

         listOfLabeledMarkers.add(new MocapMarker(markerId,
                                                  rigidBodyId,
                                                  new Vector3D(xPos, yPos, zPos),
                                                  markerSize,
                                                  markerIsOccludedInThisFrame,
                                                  positionSolvedFromPointCloud,
                                                  positionSolvedFromModel,
                                                  markerIsAssociated,
                                                  markerIsUnLabeledWithId,
                                                  markerHasActiveLEDMarker,
                                                  residualError));

         for (MocapRigidBody rb : listOfRigidbodies)
         {
            if (rb.getId() == rigidBodyId)
            {
               rb.addMocapMarker(new MocapMarker(id,
                                                 rigidBodyId,
                                                 new Vector3D(xPos, yPos, zPos),
                                                 markerId,
                                                 markerIsOccludedInThisFrame,
                                                 positionSolvedFromPointCloud,
                                                 positionSolvedFromModel,
                                                 markerIsAssociated,
                                                 markerIsUnLabeledWithId,
                                                 markerHasActiveLEDMarker,
                                                 residualError));
            }
         }
      }
   }

   private void parseMarkerSets(ByteBuffer bb)
   {
      for (int i = 0; i < numberOfMarkerSets; i++)
      {
         int startPosition = bb.position();
         byte[] buffer = new byte[bb.remaining()];
         bb.get(buffer);

         String dataSetName = new String(buffer);
         int nullPos = dataSetName.indexOf(0);
         dataSetName = dataSetName.substring(0, nullPos);

         bb.position(startPosition + dataSetName.length() + 1);

         int markers = bb.getInt();

         if (DEBUG)
            System.out.println("DataSet Name: " + dataSetName);

         MocapMarkerSet mocapMarkerSet = new MocapMarkerSet(dataSetName);
         listofMarkerSets.add(mocapMarkerSet);

         for (int j = 0; j < markers; j++)
         {
            float xPos = bb.getFloat();
            float yPos = bb.getFloat();
            float zPos = bb.getFloat();

            mocapMarkerSet.addMarker(new MocapMarker(j, new Vector3D(xPos, yPos, zPos), -1));

            if (DEBUG)
               System.out.println("Marker # " + j + " - Position: (" + xPos + ", " + yPos + ", " + zPos + ")");
         }
      }
   }

   private void parseTimeCode(ByteBuffer bb)
   {
      long timeCodeInt = (bb.getInt() & 0xFFFFFFFFL);
      long timeCodeSubFrame = (bb.getInt() & 0xFFFFFFFFL);

      int hour = (int) ((timeCodeInt >> 24) & 255);
      int min = (int) ((timeCodeInt >> 16) & 255);
      int sec = (int) ((timeCodeInt >> 8) & 255);
      int frame = (int) (timeCodeInt & 255);
      long subFrame = timeCodeSubFrame;

      //		System.out.println("Hour: " + hour);
      //		System.out.println("Min: " + min);
      //		System.out.println("Sec: " + sec);
      //		System.out.println("Frame: " + frame);
      //		System.out.println("SFrame: " + subFrame);

      timestamp = bb.getDouble();
   }

   private void parseUnLabeledMarkers(ByteBuffer bb)
   {
      for (int i = 0; i < numberOfUnLabeledMarkers; i++)
      {
         uMarkerX = bb.getFloat();
         uMarkerY = bb.getFloat();
         uMarkerZ = bb.getFloat();
         listOfUnLabeledMarkers.add(new MocapMarker(i, new Vector3D(uMarkerX, uMarkerY, uMarkerZ), -1));
      }
   }

   private void parseRigidBodies(ByteBuffer bb)
   {
      if (DEBUG)
         System.out.println("# of rigid bodies: " + numberOfRigidBodies);

      for (int i = 0; i < numberOfRigidBodies; i++)
      {
         rigidBodyId = bb.getInt();
         posX = bb.getFloat();
         posY = bb.getFloat();
         posZ = bb.getFloat();

         rotX = bb.getFloat();
         rotY = bb.getFloat();
         rotZ = bb.getFloat();
         rotS = bb.getFloat();
         meanMarkerError = bb.getFloat();

         short params = 10;
         params = bb.getShort();

         isTracked = (params & 0x01) != 0;

         listOfRigidbodies.add(new MocapRigidBody(rigidBodyId, new Vector3D(posX, posY, posZ), new Quaternion(rotX, rotY, rotZ, rotS), isTracked));

         if (DEBUG)
         {
            System.out.println("    Rigidbody # " + i + "  Position: (" + posX + ", " + posY + ", " + posZ + ")");
            System.out.println("    Rigidbody # " + i + "  Orientation: (" + rotX + ", " + rotY + ", " + rotZ + ", " + rotS + ")");
            System.out.println("Tracked: " + isTracked);
         }
      }
   }

   public void toEuler(Quaternion q)
   {
      double r11 = -2 * (q.getY() * q.getZ() - q.getS() * q.getX());
      double r12 = q.getS() * q.getS() - q.getX() * q.getX() - q.getY() * q.getY() + q.getZ() * q.getZ();
      double r21 = 2 * (q.getX() * q.getZ() + q.getS() * q.getY());
      double r31 = -2 * (q.getX() * q.getY() - q.getS() * q.getZ());
      double r32 = q.getS() * q.getS() + q.getX() * q.getX() - q.getY() * q.getY() - q.getZ() * q.getZ();

      double pitch = Math.atan2(r31, r32);
      double yaw = Math.asin(r21);
      double roll = Math.atan2(r11, r12);

      //		System.out.println(Math.toDegrees(roll));

   }

   public void set(Quaternion q1)
   {
      double sqw = q1.getS() * q1.getS();
      double sqx = q1.getX() * q1.getX();
      double sqy = q1.getY() * q1.getY();
      double sqz = q1.getZ() * q1.getZ();
      double heading = Math.atan2(2.0 * (q1.getX() * q1.getY() + q1.getZ() * q1.getS()), (sqx - sqy - sqz + sqw));
      double bank = Math.atan2(2.0 * (q1.getY() * q1.getZ() + q1.getX() * q1.getS()), (-sqx - sqy + sqz + sqw));
      double attitude = Math.asin(-2.0 * (q1.getX() * q1.getZ() - q1.getY() * q1.getS()));

      System.out.println(Math.toDegrees(heading));
      System.out.println(Math.toDegrees(bank));
      System.out.println(Math.toDegrees(attitude));
   }

   public ArrayList<MocapRigidBody> getRigidBodies()
   {
      return listOfRigidbodies;
   }

   public ArrayList<MocapMarkerSet> getMarkerSets()
   {
      return listofMarkerSets;
   }

   public ArrayList<MocapMarker> getLabeledMarkers()
   {
      return listOfLabeledMarkers;
   }

   public ArrayList<MocapMarker> getUnLabeledMarkers()
   {
      return listOfUnLabeledMarkers;
   }

   public enum MessageType
   {
      NAT_CONNECT, NAT_SERVER_INFO, NAT_REQUEST, NAT_RESPONSE, NAT_REQUEST_MODEL_DEF, NAT_MODEL_DEF, NAT_REQUEST_FRAME_OF_DATA, NAT_FRAME_OF_DATA, NAT_MESSAGE_STRING, NAT_UNRECOGNIZED_REQUEST, NAT_UNDEFINED
   }
}
