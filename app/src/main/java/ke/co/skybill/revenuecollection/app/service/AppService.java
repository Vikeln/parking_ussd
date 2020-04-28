package ke.co.skybill.revenuecollection.app.service;


import ke.co.skybill.revenuecollection.app.entities.App;
import ke.co.skybill.revenuecollection.app.models.AppRequest;
import ke.co.skybill.revenuecollection.app.models.AssignAppRequest;
import ke.co.skybill.revenuecollection.app.models.Status;
import ke.co.skybill.revenuecollection.app.utils.SingleItemResponse;


public interface AppService {

    public SingleItemResponse<App> createApp(AppRequest request);

    public SingleItemResponse<App> assignApp(AssignAppRequest request);

    public SingleItemResponse<Status> deleteApp(String name);
}
