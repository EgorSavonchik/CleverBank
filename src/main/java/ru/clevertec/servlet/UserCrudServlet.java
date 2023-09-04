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

    /**
     * Переодпределяет метод doGet для обработки get запросов
     * /user - возвращает список всех пользователей в формате json
     * /user/{id} - возвращает пользователя с заданным идентификатором в формате json
     *
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws ServletException
     * @throws IOException
     */
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

    /**
     * Переодпределяет метод doPost для обработки post запросов
     * /user - при отправке UserRequest в формате json добавляет нового пользователя в базу данных
     *
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRequest userRequest = gson.fromJson(request.getParameter("userRequest"), UserRequest.class);

        userService.create(new User(userRequest.getName(), userRequest.getPassword()));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doPut для обработки put запросов
     * /user - при отправке UserRequest в формате json обновляет существующего пользователя в базе данных
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRequest userRequest = gson.fromJson(request.getParameter("userRequest"), UserRequest.class);

        userService.update(new User(userRequest.getName(), userRequest.getPassword()),
                Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doDelete для обработки delete запросов
     * /user/{id} - удаляет пользователя с заданным идентификатором
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo().split("/").length == 2) {
            userService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}
