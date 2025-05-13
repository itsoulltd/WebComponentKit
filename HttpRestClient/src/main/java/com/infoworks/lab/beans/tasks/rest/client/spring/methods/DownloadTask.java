package com.infoworks.lab.beans.tasks.rest.client.spring.methods;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.util.services.iResourceService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.function.Consumer;

public class DownloadTask extends GetTask {

    public DownloadTask() {super();}

    public DownloadTask(String baseUri, String requestUri, Object...params) {
        super(baseUri, requestUri, params);
    }

    public DownloadTask(String baseUri, String requestUri, Consumer<String> response) {
        super(baseUri, requestUri, response);
    }

    @Override
    public ResourceResponse execute(Message message) throws RuntimeException {
        RestTemplate template = getTemplate();
        try {
            ResponseEntity<Resource> response = (getParams().length > 0)
                    ? template.exchange(getUri(), HttpMethod.GET, getBody(), Resource.class, getParams())
                    : template.exchange(getUri(), HttpMethod.GET, getBody(), Resource.class);
            if (getResponseListener() != null) {
                String base64Encoded = null;
                Resource resource = response.getBody();
                if (resource != null && resource.getInputStream() != null) {
                    iResourceService service = iResourceService.create();
                    byte[] bytes = service.readAsBytes(resource.getInputStream());
                    base64Encoded = new String(Base64.getEncoder().encode(bytes), "UTF-8");
                }
                getResponseListener().accept(base64Encoded);
            }
            return (ResourceResponse) new ResourceResponse()
                    .setResource(response.getBody())
                    .setStatus(200)
                    .setMessage(getUri());
        } catch (Exception e) {
            return (ResourceResponse) new ResourceResponse()
                    .setResource(null)
                    .setStatus(500)
                    .setMessage(getUri())
                    .setError(e.getMessage());
        }
    }

    public static class ResourceResponse extends Response {
        private Resource resource;

        public Resource getResource() {
            return resource;
        }

        public ResourceResponse setResource(Resource resource) {
            this.resource = resource;
            return this;
        }
    }
}
