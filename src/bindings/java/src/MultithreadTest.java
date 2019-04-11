import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;

public class MultithreadTest
{ 
    public static void main(String[] args) throws Exception
    {
        String country = "", configfile = "", runtimeDataDir = "", licensePlate = "";
        Integer numThreads = 1;
        if (args.length == 5) 
        {
            country = args[0];
            configfile = args[1];
            runtimeDataDir = args[2];
            licensePlate = args[3];
            numThreads = Integer.parseInt(args[4]);
        }
        else
        {
            System.err.println("Program requires 5 arguments: Country, Config File, runtime_data dir, license plate image, and number of threads to execute");
            System.exit(1);
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++)
        {
            executor.submit(new AlprRunnable(country, configfile, runtimeDataDir, licensePlate));
        }
        
        executor.shutdown();
    }
    
    public static class AlprRunnable implements Runnable
    {
        private String _licensePlateImage;
        private Alpr _alpr;
        
        public AlprRunnable(String country, String configFile, String runtimeDataDir, String licensePlateImage)
        {
            _licensePlateImage = licensePlateImage;
            _alpr = new Alpr(country, configFile, runtimeDataDir);
            _alpr.setTopN(10);
        }

        @Override
        public void run()
        {
            try
            {
                // Read an image into a byte array and send it to OpenALPR
                Path path = Paths.get(_licensePlateImage);
                byte[] imagedata = Files.readAllBytes(path);

                AlprResults results = _alpr.recognize(imagedata);

                System.out.println("Processing Time: " + results.getTotalProcessingTimeMs() + " ms");
                System.out.println("Found " + results.getPlates().size() + " results");

                System.out.format("  %-15s%-8s\n", "Plate Number", "Confidence");
                for (AlprPlateResult result : results.getPlates())
                {
                    for (AlprPlate plate : result.getTopNPlates()) {
                        if (plate.isMatchesTemplate())
                            System.out.print("  * ");
                        else
                            System.out.print("  - ");
                        System.out.format("%-15s%-8f\n", plate.getCharacters(), plate.getOverallConfidence());
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("Failed to read license plate due to - " + e);
            }
            finally
            {
                _alpr.unload();
            }
        }
        
    }

}
