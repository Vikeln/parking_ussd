package ke.co.skybill.revenuecollection.customer.controllers;


import io.swagger.annotations.ApiOperation;
import ke.co.skybill.revenuecollection.customer.entities.Country;
import ke.co.skybill.revenuecollection.customer.models.AppCountryModel;
import ke.co.skybill.revenuecollection.customer.entities.AppCountry;
import ke.co.skybill.revenuecollection.customer.repositories.AppCountryDao;
import ke.co.skybill.revenuecollection.customer.repositories.CountryDao;
import ke.co.skybill.revenuecollection.customer.security.ApiPrincipal;
import ke.co.skybill.revenuecollection.customer.utils.Response;
import ke.co.skybill.revenuecollection.customer.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("countries")
public class CountryController {

    private static final Logger log = LoggerFactory.getLogger(CountryController.class.getName());


    @Autowired
    private AppCountryDao appCountryDao;

    @Autowired
    private CountryDao countryDao;

    @GetMapping
    @ApiOperation(value = "Get all app countries")
    public ResponseEntity getAll(
            @RequestParam(value = "direction", defaultValue = Util.Pagination.DEFAULT_ORDER_DIRECTION) String direction,
            @RequestParam(value = "oderBy", defaultValue = Util.Pagination.DEFAULT_ORDER_BY) String orderBy,
            @RequestParam(value = "page", defaultValue = Util.Pagination.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Util.Pagination.DEFAULT_PAGE_SIZE) int size,
            @ApiIgnore @AuthenticationPrincipal ApiPrincipal principal, HttpServletRequest httpServletRequest) {

        Pageable pageable = Util.getPageable(page, size, direction, orderBy);
        log.info("logged user {}" , Util.toJson(principal.getUser()));
        Page<AppCountry> countries = appCountryDao.findAllByApp(pageable,httpServletRequest.getHeader("App-Key"));
        List<AppCountryModel> countryModels = new ArrayList<>();
        for (AppCountry country : countries)
            countryModels.add(AppCountryModel.tranform(country));
        return Util.getResponse(Response.SUCCESS.status(), Util.getResponse(countries, countryModels));
    }

    @GetMapping("all")
    @ApiOperation(value = "Get all countries")
    public ResponseEntity getAllCountries(
            @ApiIgnore @AuthenticationPrincipal ApiPrincipal principal) {

        List<Country> countries = countryDao.findAll();
        return Util.getResponse(Response.SUCCESS.status(), countries);

    }

    }
