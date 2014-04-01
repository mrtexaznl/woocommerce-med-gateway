/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mediterraneancoin.payment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author marco
 */
public class PaymentMainServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
/*
param currency_code: MED 
param state: TS 
param address2:  
param zip: 34100 
param address1: Via test 1 
param item_number: 10 
param country: IT 
param city: Trieste 
param amount: 950 
param first_name: Marco 
param email: admin@netlawsrl.com 
param merchant_id: 1234 
param item_name: Payment for order - 10 
param last_name: T. 
 */        
        
        System.out.println(new Date() + " " + "PaymentMainServlet" + request.getMethod());
        
        //if (!request.getParameter("currency_code").equals(null)) {
        if (true) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/faces/payment.xhtml");
            
            requestDispatcher.forward(request, response);
            
            return;
        }
        
        Enumeration<String> parameterNames = request.getParameterNames();
	 
        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
            System.out.print("param " + paramName + ": ");

            String[] paramValues = request.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                System.out.print(paramValue + " ");
            }
            
            System.out.println();

        }        
        
        
        
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PaymentMainServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PaymentMainServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
