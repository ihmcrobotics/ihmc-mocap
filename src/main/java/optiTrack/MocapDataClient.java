package optiTrack;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public class MocapDataClient
{
   public static int NETWORK_IF_TO_USE = -1;
   private static MocapDataClient mocapDataClientSingleton;
   private final AtomicBoolean running = new AtomicBoolean(false);
   protected ArrayList<MocapRigidbodiesListener> listOfMocapRigidBodiesListeners = new ArrayList<MocapRigidbodiesListener>();
   protected ArrayList<MocapMarkerSetListener> listOfMocapMarkerSetListeners = new ArrayList<MocapMarkerSetListener>();
   protected ArrayList<MocapLabeledMarkersListener> listOfMocapLabeledMarkersListeners = new ArrayList<MocapLabeledMarkersListener>();
   protected ArrayList<MocapUnLabeledMarkersListener> listOfMocapUnLabeledMarkersListeners = new ArrayList<MocapUnLabeledMarkersListener>();
   protected double frequency;
   protected long lastTime = 0;
   boolean firstTime = true;
   ArrayList<String> listOfModels = new ArrayList<>();
   long lastTimeWarningWasIssued = 0l;
   private MulticastSocket dataSocket;
   private DatagramSocket commandSocket;
   private CallFrequencyCalculator callFrequencyCalculator;
   private int socketTimeout = 50;
   private int receivingBufferSize = 9500;
   // Do not change these
   private int dataPort = 1511;
   private int commandPort = 1510;
   private String mocapIP = "239.255.42.99";
   private NetworkInterface networkInterface;

   public MocapDataClient(int networkInterfaceToUse)
   {
      try
      {
         running.set(true);

         System.out.println("\n\nMOCAP: Initialized");
         NETWORK_IF_TO_USE = networkInterfaceToUse;
         callFrequencyCalculator = new CallFrequencyCalculator();
         Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

         System.out.println("MOCAP: Network adapters found:\n-----------------------------------");

         while (enumeration.hasMoreElements())
         {
            NetworkInterface networkInterface = enumeration.nextElement();

            System.out.println("MOCAP: - Index: " + networkInterface.getIndex() + " Interface Name: " + networkInterface.getDisplayName());
         }

         System.out.println("\nMOCAP: Starting MOCAP client on network interface " + NETWORK_IF_TO_USE);

         if (NETWORK_IF_TO_USE >= 0)
         {
            networkInterface = NetworkInterface.getByIndex(NETWORK_IF_TO_USE);
            System.out.println("MOCAP: Network Interface Name: " + networkInterface.getDisplayName());
         }
         else if (NETWORK_IF_TO_USE == -1)
         {
            System.out.println(
                  "MOCAP: An index of -1 means that the system will automatically select the network interface. Auto select might not always work...");
         }

         commandSocket = new DatagramSocket();
         commandSocket.setBroadcast(true);

         //			Thread udpCommandThread = new Thread(new MocapCommandThread());
         //			udpCommandThread.start();
         //			System.out.println("MOCAP: Command Thread was started");

         System.out.println("MOCAP: Using a socket timeout of " + socketTimeout + "ms");

         InetAddress mcastAddr = InetAddress.getByName(mocapIP);
         InetSocketAddress group = new InetSocketAddress(mcastAddr, dataPort);
         dataSocket = new MulticastSocket(dataPort);
         dataSocket.setSoTimeout(socketTimeout);
         dataSocket.joinGroup(group, networkInterface);

         lastTime = System.currentTimeMillis();

         System.out.println("MOCAP: Starting data thread");
         Thread dataReceivingThread = new Thread(new MocapDataReceivingThread());
         dataReceivingThread.start();
         System.out.println("MOCAP: Data thread started on port " + dataPort);

         // socket.leaveGroup(group);
         // socket.close();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public MocapDataClient()
   {
      this(-1);
   }

   public static MocapDataClient getInstance() throws Exception
   {
      if (mocapDataClientSingleton == null)
      {
         mocapDataClientSingleton = new MocapDataClient();
      }

      return mocapDataClientSingleton;
   }

   public static void main(String args[])
   {
      MocapDataClient udpMulticastClient = new MocapDataClient();
   }

   public void shutdown()
   {
      running.set(false);
   }

   public void sendFrameRequest() throws IOException
   {
      System.out.println("MOCAP: Sending Frame Request Request to server");

      ByteBuffer buffer = ByteBuffer.allocate(4);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putShort((short) 6);
      buffer.putShort((short) 0);

      DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length, InetAddress.getByName(mocapIP), commandPort);
      commandSocket.send(packet);
   }

   public void connect() throws IOException
   {
      System.out.println("MOCAP: Sending Connect Request to server");

      ByteBuffer buffer = ByteBuffer.allocate(4);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putShort((short) 0);
      buffer.putShort((short) 0);

      DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length, InetAddress.getByName(mocapIP), commandPort);
      commandSocket.send(packet);
   }

   public ArrayList<String> getAvailableModels()
   {
      return listOfModels;
   }

   public double getMocapDataReceivingFrequency()
   {
      return frequency;
   }

   protected void updateListeners(ArrayList<MocapRigidBody> lisftOfRigidbodies,
                                  ArrayList<MocapMarkerSet> lisftOfMocapMarkerSets,
                                  ArrayList<MocapMarker> listOfLabeledMocapMarkers,
                                  ArrayList<MocapMarker> lisftOfUnlabeledMocapMarkers)
   {
      updateRigidBodiesListeners(lisftOfRigidbodies);
      updateMocapMarkerSetListeners(lisftOfMocapMarkerSets);
      updateLabeledMocapMarkersListeners(listOfLabeledMocapMarkers);
      updateUnLabeledMocapMarkersListeners(lisftOfUnlabeledMocapMarkers);
   }

   protected void updateRigidBodiesListeners(ArrayList<MocapRigidBody> listOfRigidbodies)
   {
      frequency = callFrequencyCalculator.determineCallFrequency();

      if (System.currentTimeMillis() - lastTime > 5000)
      {
         if (frequency < 80) // Should always be around 99
         {
            System.err.println("**MOCAP WARNING** - Receiving data rate is less than 95Hz >>>> " + frequency);
         }
      }

      ArrayList<MocapRigidbodiesListener> listeners = (ArrayList<MocapRigidbodiesListener>) listOfMocapRigidBodiesListeners.clone();
      for (MocapRigidbodiesListener listener : listeners)
      {
         listener.updateRigidbodies(listOfRigidbodies);
      }
   }

   protected void updateMocapMarkerSetListeners(ArrayList<MocapMarkerSet> listOfMarkerSets)
   {
      ArrayList<MocapMarkerSetListener> listeners = (ArrayList<MocapMarkerSetListener>) listOfMocapMarkerSetListeners.clone();

      for (MocapMarkerSetListener listener : listeners)
      {
         listener.updateMocapMarkerSets(listOfMarkerSets);
      }
   }

   protected void updateLabeledMocapMarkersListeners(ArrayList<MocapMarker> listOfLabeledMarkers)
   {
      ArrayList<MocapLabeledMarkersListener> listeners = (ArrayList<MocapLabeledMarkersListener>) listOfMocapLabeledMarkersListeners.clone();

      for (MocapLabeledMarkersListener listener : listeners)
      {
         listener.updateMocapLabeledMarkers(listOfLabeledMarkers);
      }
   }

   protected void updateUnLabeledMocapMarkersListeners(ArrayList<MocapMarker> listOfUnLabeledMarkers)
   {
      ArrayList<MocapUnLabeledMarkersListener> listeners = (ArrayList<MocapUnLabeledMarkersListener>) listOfMocapUnLabeledMarkersListeners.clone();

      for (MocapUnLabeledMarkersListener listener : listeners)
      {
         listener.updateMocapUnLabeledMarkers(listOfUnLabeledMarkers);
      }
   }

   public void registerRigidBodiesListener(MocapRigidbodiesListener listener)
   {
      listOfMocapRigidBodiesListeners.add(listener);
   }

   public void registerMocapMarkerSetListener(MocapMarkerSetListener listener)
   {
      listOfMocapMarkerSetListeners.add(listener);
   }

   public void registerMocapLabeledMarkersListener(MocapLabeledMarkersListener listener)
   {
      listOfMocapLabeledMarkersListeners.add(listener);
   }

   public void registerMocapUnLabeledMarkersListener(MocapUnLabeledMarkersListener listener)
   {
      listOfMocapUnLabeledMarkersListeners.add(listener);
   }

   protected class MocapDataReceivingThread implements Runnable
   {
      public void run()
      {
         while (running.get())
         {
            try
            {
               byte[] buf = new byte[receivingBufferSize];
               DatagramPacket packet = new DatagramPacket(buf, buf.length);

               dataSocket.receive(packet);

               MocapFrameDataPacket recvPacket = new MocapFrameDataPacket(buf);

               ArrayList<MocapRigidBody> lisftOfRigidbodies = recvPacket.getRigidBodies();
               ArrayList<MocapMarkerSet> listOfMarkerSets = recvPacket.getMarkerSets();
               ArrayList<MocapMarker> listOfLabeledMocapMarkers = recvPacket.getLabeledMarkers();
               ArrayList<MocapMarker> listOfUnlabeledMocapMarker = recvPacket.getUnLabeledMarkers();

               if (firstTime)
               {
                  System.out.println("MOCAP: Data was received from MOCAP Server! # Of Rigid bodies detected: " + lisftOfRigidbodies.size());
                  firstTime = false;
                  listOfModels = new ArrayList<>();

                  for (MocapRigidBody rb : lisftOfRigidbodies)
                  {
                     listOfModels.add("" + rb.getId());
                  }
               }

               updateListeners(lisftOfRigidbodies, listOfMarkerSets, listOfLabeledMocapMarkers, listOfUnlabeledMocapMarker);
            }
            catch (IOException e)
            {
               if (System.currentTimeMillis() - lastTimeWarningWasIssued > 1000)
               {
                  System.err.println(
                        "**MOCAP WARNING** - Socket Timeout - No Rigibodies are being transmitted from MOCAP SERVER. Make sure streaming is enabled!");
                  lastTimeWarningWasIssued = System.currentTimeMillis();
               }
            }
         }

         dataSocket.close();
         commandSocket.close();
      }
   }

   protected class MocapCommandThread implements Runnable
   {
      public void run()
      {
         try
         {
            connect();
            sendFrameRequest();
         }
         catch (IOException e1)
         {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }

         while (true)
         {
            try
            {

               DatagramPacket packet;
               byte[] buf = new byte[2000];
               packet = new DatagramPacket(buf, buf.length);
               commandSocket.receive(packet);

               MocapFrameDataPacket recvPacket = new MocapFrameDataPacket(buf);
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   public class CallFrequencyCalculator
   {
      private double callFrequency;
      private double requestDeltaTInMilliseconds;
      private int counter;
      private double lastTimeCalled = 0.0;
      private double currentTime = 0.0;
      private int numberOfSamples = 10;
      private double frequencyAddition = 0;

      public void setNumberOfSamples(int numberOfSamples)
      {
         this.numberOfSamples = numberOfSamples;
      }

      public double determineCallFrequency()
      {
         currentTime = System.nanoTime();

         double frequency = 1.0 / ((currentTime - lastTimeCalled) / 1.0E9);
         requestDeltaTInMilliseconds = (currentTime - lastTimeCalled) / 1.0E6;
         frequencyAddition = frequencyAddition + frequency;
         lastTimeCalled = currentTime;

         if (counter > numberOfSamples)
         {
            callFrequency = frequencyAddition / counter;
            counter = 1;
            frequencyAddition = 0;
         }
         else
         {
            counter++;
         }

         return callFrequency;
      }
   }
}
