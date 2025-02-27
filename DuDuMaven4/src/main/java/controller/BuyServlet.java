

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.ProductDAO;
import dal.ShopDAO;
import dal.WarehouseDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Cart;
import model.Item;
import model.Product;
import model.Shop;
import model.User;

/**
 *
 * @author Pham Thang
 */
@WebServlet(name="BuyServlet", urlPatterns={"/buy"})
public class BuyServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BuyServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BuyServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
          String typeCart = request.getParameter("type");
        String sizeCart = request.getParameter("size");
        String productID_raw = request.getParameter("productID");
        String quantityCart_raw = request.getParameter("quantity");
        if (sizeCart == null || sizeCart.equalsIgnoreCase("null")) {
            sizeCart = "";
        }
        if (typeCart == null || typeCart.equalsIgnoreCase("null")) {
            typeCart = "";
        }

        int productID = 0, quantityCart = 0;
        try {
            productID = Integer.parseInt(productID_raw);
            quantityCart = Integer.parseInt(quantityCart_raw);

        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        ProductDAO pd = new ProductDAO();
        Product p = pd.getProductByProductID(productID);
        List<Product> related = pd.getRelatedProduct(p.getShop().getShopID(), productID);
        WarehouseDAO wd = new WarehouseDAO();
        
        int quantity = wd.getQuantityByID(productID);
        List<String> type = wd.getTypeByID(productID);
        List<String> size = wd.getSizeByID(productID);
        boolean[] t = new boolean[type.size()];
        boolean[] s = new boolean[size.size()];
        for (int i = 0; i < type.size(); i++) {
            if (wd.getQuantityByIDType(productID, type.get(i)) == 0) {
                t[i] = false;
            } else {
                t[i] = true;
            }
        }
        for (int i = 0; i < size.size(); i++) {
            if (wd.getQuantityByIDSize(productID, size.get(i)) == 0) {
                s[i] = false;
            } else {
                s[i] = true;
            }
        }
        boolean checkType = false;
        boolean checkSize = false;
        if (type.size() != 0) {
            checkType = true;
        }
        if (size.size() != 0) {
            checkSize = true;
        }
        
        Cookie[] arr = request.getCookies();
        String txt = "";
        if (arr != null) {
            for (Cookie o : arr) {
                if (o.getName().equals("cart")) {
                    txt += o.getValue();
                    o.setMaxAge(0);
                    response.addCookie(o);
                }
            }
        }
        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");
        if (txt.isEmpty()) {
            txt = productID + ":" + typeCart + ":" + sizeCart + ":" + quantityCart + ":" + account.getUserID();
        } else {
            txt = txt + "/" + productID + ":" + typeCart + ":" + sizeCart + ":" + quantityCart + ":" + account.getUserID();
        }
        Cookie c = new Cookie("cart", txt);
        c.setMaxAge(2 * 24 * 60 * 60);
        response.addCookie(c);
        Cart cart = new Cart(txt, account.getUserID());
        int numberCart = cart.getItemsByUserID(account.getUserID()).size();
                 
        
        
        
        
        
         Cookie[] arr1 = request.getCookies();
        String txt1 = "";
        if (arr1 != null) {
            for (Cookie o1 : arr1) {
                if (o1.getName().equals("cart")) {
                    txt += o1.getValue();
                }
            }
        }
        Cart cart1 = null;
        Map<Shop, List<Item>> itemsByShop = new HashMap<>();

        ShopDAO sd = new ShopDAO();
        cart1 = new Cart(txt, account.getUserID());
        List<Item> listItem = cart.getItems();
        int n=listItem.size();
        // tạo 1 map chứa các item
        for (Item item : listItem) {
            Shop sh = sd.getShopByID(item.getProduct().getShop().getShopID());

            if (!itemsByShop.containsKey(sh)) {
                itemsByShop.put(sh, new ArrayList<Item>());
            }

            itemsByShop.get(sh).add(item);
        }
        request.setAttribute("productID", productID);
        request.setAttribute("whatatype", typeCart);
        request.setAttribute("whatasize", sizeCart);
        request.setAttribute("itemsByShop", itemsByShop);
        request.setAttribute("cart", cart);
        request.setAttribute("sizeCart", n);
        request.getRequestDispatcher("show.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}









