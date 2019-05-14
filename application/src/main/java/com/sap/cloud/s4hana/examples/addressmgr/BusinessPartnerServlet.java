package com.sap.cloud.s4hana.examples.addressmgr;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.cloud.s4hana.examples.addressmgr.commands.GetAllBusinessPartnersCommand;
import com.sap.cloud.s4hana.examples.addressmgr.commands.GetSingleBusinessPartnerByIdCommand;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@WebServlet("/api/business-partners")
public class BusinessPartnerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger Logger = CloudLoggerFactory.getLogger(BusinessPartnerServlet.class);

    private final BusinessPartnerService service = new DefaultBusinessPartnerService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
            final String id = request.getParameter("id");

            final String jsonResult;
            try {
                if(id == null) {
                    final List<BusinessPartner> result = new GetAllBusinessPartnersCommand(service).execute();
                    jsonResult = new Gson().toJson(result);
                } else {
                    if(!validateInput(id)){
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            String.format("Invalid business partner ID '%s'. " + 
                                            "Business partner ID must not be empty or longer than 10 characters.", id));
                        return;
                    }
                    final BusinessPartner result = new GetSingleBusinessPartnerByIdCommand(service, id).execute();
                    jsonResult = new Gson().toJson(result);
                }
                response.setContentType("application/json");
                response.getWriter().write(jsonResult);
            
        }catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occured while handling request: " + e.toString());
        }
    }

    private boolean validateInput(String id) {
        return !Strings.isNullOrEmpty(id) && id.length() <= 10;
    }
}