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

    /**
     * Переодпределяет метод doGet для обработки get запросов
     * /transaction - возвращает список всех транзакций в формате json
     * /transaction/{id} - возвращает транзакцию с заданным идентификатором в формате json
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
            printWriter.write(gson.toJson(transactionService.findAll()));
        } else if(request.getPathInfo().split("/").length == 2) {
            printWriter.write(gson.toJson(transactionService.findById(Integer.parseInt(
                    request.getPathInfo().split("/")[1]))));
        }
    }

    /**
     * Переодпределяет метод doPost для обработки post запросов
     * /transaction - при отправке  TransactionRequest в формате json добавляет новую транзакцию в базу данных
     *
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TransactionRequest transactionRequest = gson.fromJson(request.getParameter("transactionRequest"),
                TransactionRequest.class);

        transactionService.create(new Transaction(transactionRequest.getSenderAccountId(),
                transactionRequest.getBeneficiaryAccountId(), transactionRequest.getAmount(),
                transactionRequest.getCreatedAt(), transactionRequest.getOperationType()));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doPut для обработки put запросов
     * /transaction - при отправке TransactionRequest в формате json обновляет существующую транзакцию в базе данных
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     *
     * @throws ServletException
     * @throws IOException
     */
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

    /**
     * Переодпределяет метод doDelete для обработки delete запросов
     * /transaction/{id} - удаляет транзакцию с заданным идентификатором
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
            transactionService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}
