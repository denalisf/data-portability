package org.dataportabilityproject.webapp;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dataportabilityproject.ServiceProviderRegistry;
import org.dataportabilityproject.shared.PortableDataType;
import org.dataportabilityproject.webapp.job.JobManager;
import org.dataportabilityproject.webapp.job.PortabilityJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for the list services available for export and import. */
@RestController
public class ListServicesController {
  @Autowired
  private ServiceProviderRegistry serviceProviderRegistry;
  @Autowired
  private JobManager jobManager;

  /** Returns of the list of data types allowed for inmport and export. */
  @CrossOrigin(origins = "http://localhost:3000")
  @RequestMapping("/_/listServices")
  @ResponseBody
  public Map<String, List<String>> listServices(HttpServletRequest request,
      @RequestParam("dataType") String type,
      @CookieValue(value = "jobToken", required = true) String token) throws Exception {
    System.out.println("listServicesForExport, token: " + token);

    // TODO: move to interceptor to redirect
    Preconditions.checkArgument(!Strings.isNullOrEmpty(token), "Token required");

    // Valid job must be present
    PortabilityJob job = jobManager.findExistingJob(token);
    Preconditions.checkState(null != job, "existingJob not found for token: %s", token);

    Optional<PortableDataType> dataType = Enums.getIfPresent(PortableDataType.class, type);
    Preconditions.checkArgument(dataType.isPresent(), "Data type required");

    PortabilityJob updatedJob = job.toBuilder().setDataType(dataType.get().name()).build();
    jobManager.updateJob(updatedJob);

    List<String> exportServices = serviceProviderRegistry.getServiceProvidersThatCanExport(dataType.get());
    List<String> importServices = serviceProviderRegistry.getServiceProvidersThatCanImport(dataType.get());
    if (exportServices.isEmpty() || importServices.isEmpty()) {
      // TODO: log a warning
    }
    return ImmutableMap.<String, List<String>>of(JsonKeys.EXPORT, exportServices, JsonKeys.IMPORT, importServices);
  }
}