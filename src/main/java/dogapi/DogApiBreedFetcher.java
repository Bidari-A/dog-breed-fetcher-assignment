package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException  {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        // return statement included so that the starter code can compile and run.

            if(breed.isEmpty()){
                throw new BreedNotFoundException("Breed not found");
            }

            String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new BreedNotFoundException("HTTP " + response.code() + " for " + breed);
                }

                ResponseBody body = response.body();
                if (body == null) {
                    throw new BreedNotFoundException("Empty response for " + breed);
                }
                JSONObject json = new JSONObject(body.string());
                String status = json.optString("status", "");
                if (!"success".equalsIgnoreCase(status)) {
                    String msg = json.optString("message", "Breed not found");
                    throw new BreedNotFoundException(msg);
                }
                JSONArray arr = json.getJSONArray("message");
                List<String> subs = new ArrayList<>(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    subs.add(arr.getString(i));
                }
                return subs;
            } catch (IOException e) {
                throw new BreedNotFoundException("Network or parsing error for " + breed);
            }


    }
}