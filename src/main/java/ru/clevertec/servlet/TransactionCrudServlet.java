package ru.clevertec.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.clevertec.dto.TransactionRequest;
import ru.clevertec.model.Transaction;
import ru.clevertec.service.TransactionService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/transaction/*")
public class TransactionCrudServlet extends HttpServlet {
    private TransactionService transactionService = new TransactionService();
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter printWriter = response.getWriter();

        if(request.getPathInfo() == null) {
            printWriter.write(gson.toJson(transactionService.findAll()));
        } else if(request.getPathInfo().split("/").length == 2) {
            printWriter.write(gson.toJson(transactionService.findById(Integer.parseInt(
                    request.getPathInfo().split("/")[1]))));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TransactionRequest transactionRequest = gson.fromJson(request.getParameter("transactionRequest"),
                TransactionRequest.class);

        transactionService.create(new Transaction(transactionRequest.getSenderAccountId(),
                transactionRequest.getBeneficiaryAccountId(), transactionRequest.getAmount(),
                transactionRequest.getCreatedAt(), transactionRequest.getOperationType()));

        response.setStatus(201);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TransactionRequest transactionRequest = gson.fromJson(request.getParameter("transactionRequest"),
                TransactionRequest.class);

        transactionService.update(new Transaction(transactionRequest.getSenderAccountId(),
                transactionRequest.getBeneficiaryAccountId(), transactionRequest.getAmount(),
                transactionRequest.getCreatedAt(), transactionRequest.getOperationType()),
                Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo().split("/").length == 2) {
            transactionService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}
