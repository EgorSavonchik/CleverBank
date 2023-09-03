package ru.clevertec.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.clevertec.dto.UserRequest;
import ru.clevertec.model.User;
import ru.clevertec.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/user/*")
public class UserCrudServlet extends HttpServlet {
    private UserService userService = new UserService();
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter printWriter = response.getWriter();

        if(request.getPathInfo() == null) {
            printWriter.write(gson.toJson(userService.findAll()));
        } else if(request.getPathInfo().split("/").length == 2) {
            printWriter.write(gson.toJson(userService.findById(Integer.parseInt(
                    request.getPathInfo().split("/")[1]))));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRequest userRequest = gson.fromJson(request.getParameter("userRequest"), UserRequest.class);

        userService.create(new User(userRequest.getName(), userRequest.getPassword()));

        response.setStatus(201);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRequest userRequest = gson.fromJson(request.getParameter("userRequest"), UserRequest.class);

        userService.update(new User(userRequest.getName(), userRequest.getPassword()),
                Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo().split("/").length == 2) {
            userService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}
