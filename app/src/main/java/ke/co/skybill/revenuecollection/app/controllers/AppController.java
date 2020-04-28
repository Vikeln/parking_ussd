package ke.co.skybill.revenuecollection.app.controllers;

import io.swagger.annotations.ApiOperation;
import ke.co.skybill.revenuecollection.app.entities.App;
import ke.co.skybill.revenuecollection.app.models.AppRequest;
import ke.co.skybill.revenuecollection.app.models.AssignAppRequest;
import ke.co.skybill.revenuecollection.app.security.ApiPrincipal;
import ke.co.skybill.revenuecollection.app.security.CurrentUser;
import ke.co.skybill.revenuecollection.app.service.AppService;
import ke.co.skybill.revenuecollection.app.utils.Response;
import ke.co.skybill.revenuecollection.app.utils.SingleItemResponse;
import ke.co.skybill.revenuecollection.app.repository.AppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("app")
public class AppController {
    @Autowired
    private AppDao appDao;

    @Autowired
    private AppService appService;

    @PostMapping
    @ApiOperation(value = "Create App")
    public ResponseEntity createApp(@RequestBody AppRequest request,
                                    @ApiIgnore @CurrentUser ApiPrincipal apiPrincipal) {
        return ResponseEntity.ok().body(appService.createApp(request));
    }


    @PostMapping("assign-gateway-data")
    @ApiOperation(value = "Assign gateway data to an App ")
    public ResponseEntity assignApp(@RequestBody AssignAppRequest request,
                                    @ApiIgnore @CurrentUser ApiPrincipal apiPrincipal) {
        return ResponseEntity.ok().body(appService.assignApp(request));
    }


    @GetMapping
    @ApiOperation(value = "Get all Apps")
    public SingleItemResponse<App> getAllApps(@ApiIgnore @CurrentUser ApiPrincipal apiPrincipal) {
        return new SingleItemResponse(Response.SUCCESS.status(),appDao.findAll());
    }

    @DeleteMapping("/{name}")
    @ApiOperation(value = "Delete an app using its name")
    public SingleItemResponse deleteApp(@PathVariable("name") String name, @ApiIgnore @CurrentUser ApiPrincipal principal) {
        return appService.deleteApp(name);
    }
}
