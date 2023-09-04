package ru.clevertec.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.clevertec.dto.BankRequest;
import ru.clevertec.model.Bank;
import ru.clevertec.service.BankService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/bank/*")
public class BankCrudServlet extends HttpServlet {
    private BankService bankService = new BankService();
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Переодпределяет метод doGet для обработки get запросов
     * /bank - возвращает список всех банков в формате json
     * /bank/{id} - возвращает банк с заданным идентификатором в формате json
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
            printWriter.write(gson.toJson(bankService.findAll()));
        } else if(request.getPathInfo().split("/").length == 2) {
            printWriter.write(gson.toJson(bankService.findById(Integer.parseInt(
                    request.getPathInfo().split("/")[1]))));
        }
    }

    /**
     * Переодпределяет метод doPost для обработки post запросов
     * /bank - при отправке BankRequest в формате json добавляет новый банк в базу данных
     *
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BankRequest bankRequest = gson.fromJson(request.getParameter("bankRequest"), BankRequest.class);

        bankService.create(new Bank(bankRequest.getName()));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doPut для обработки put запросов
     * /bank - при отправке BankRequest в формате json обновляет существующий банк в базе данных
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BankRequest bankRequest = gson.fromJson(request.getParameter("bankRequest"), BankRequest.class);

        bankService.update(new Bank(bankRequest.getName()), Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doDelete для обработки delete запросов
     * /bank/{id} - удаляет банк с заданным идентификатором
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
            bankService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}

