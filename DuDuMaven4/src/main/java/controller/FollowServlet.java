/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import com.google.cloud.storage.Acl;
import dal.FollowDAO;
import dal.UserDAO;
import dal.videoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;
import model.VideoManage;

/**
 *
 * @author PC
 */
@WebServlet(name="FollowServlet", urlPatterns={"/follow"})
public class FollowServlet extends HttpServlet {
   
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
            out.println("<title>Servlet FollowServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet FollowServlet at " + request.getContextPath () + "</h1>");
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
        HttpSession session = request.getSession();
        UserDAO ud = new UserDAO();
        FollowDAO fd = new FollowDAO();
        videoDAO vd = new videoDAO();
        User u = (User) session.getAttribute("account");
        int userID = u.getUserID();
        String userid = request.getParameter("userID");
        String follow = request.getParameter("follow1");
        int peopleID;
        try {
            peopleID = Integer.parseInt(userid);
             User people = ud.getUserbyID(peopleID);
            
              List<VideoManage> listVideo = vd.getVIdeoByUserID(peopleID);
            request.setAttribute("listVideo", listVideo);
             request.setAttribute("videosize", listVideo.size());
            if(follow.equals("Follow")) fd.insertFollow(userID, peopleID);
            else fd.deleteFollw(userID, peopleID);
            if (fd.checkFollow(userID, peopleID)) {
                request.setAttribute("followornot", "Unfollow");
            } else {
                request.setAttribute("followornot", "Follow");

            }
            request.setAttribute("people", people);
            List<User> listoffollowing = fd.getFollowingByUserId(peopleID);
            List<User> listoffollowers = fd.getFollowersByUserId(peopleID);
            if(listoffollowers.isEmpty())
            {
                request.setAttribute("nofollowers", "No Followers");
            }
            if(listoffollowing.isEmpty())
            {
                request.setAttribute("nofollowing", "No Following");
            }
            
            request.setAttribute("blockstatus", "Block");
            
            request.setAttribute("listoffollowing", listoffollowing);
            request.setAttribute("listoffollowers", listoffollowers);
            request.setAttribute("tottalfollowing", listoffollowing.size());
            request.setAttribute("tottalfollowers", listoffollowers.size());
        } catch (Exception e) {
        }
        request.getRequestDispatcher("peopleprofile.jsp").forward(request, response);
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
