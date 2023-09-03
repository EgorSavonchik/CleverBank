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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BankRequest bankRequest = gson.fromJson(request.getParameter("bankRequest"), BankRequest.class);

        bankService.create(new Bank(bankRequest.getName()));

        response.setStatus(201);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BankRequest bankRequest = gson.fromJson(request.getParameter("bankRequest"), BankRequest.class);

        bankService.update(new Bank(bankRequest.getName()), Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo().split("/").length == 2) {
            bankService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}

