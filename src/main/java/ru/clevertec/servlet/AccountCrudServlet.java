package ru.clevertec.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.clevertec.dto.AccountRequest;
import ru.clevertec.model.Account;
import ru.clevertec.service.AccountService;
import ru.clevertec.service.CheckService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/account/*")
public class AccountCrudServlet extends HttpServlet {
    private AccountService accountService = new AccountService();
    private CheckService checkService = new CheckService();
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Переодпределяет метод doGet для обработки get запросов
     * /account - возвращает список всех счетов в формате json
     * /account/{id} - возвращает счет с заданным идентификатором в формате json
     * /account/statement/{id}?startPeriod=?&endPeriod=? - генерирует выписку по приходу и расходу валюты в промежутке
     * времени заданном параметрами periodStart и periodEnd
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
            printWriter.write(gson.toJson(accountService.findAll()));
        } else if(request.getPathInfo().split("/").length == 2) {
            printWriter.write(gson.toJson(accountService.findById(Integer.parseInt(
                    request.getPathInfo().split("/")[1]))));
        } else if(request.getPathInfo().split("/").length == 3
                && request.getPathInfo().split("/")[1].equals("statement")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            checkService.generateStatementMoney(accountService.findById(
                    Integer.parseInt(request.getPathInfo().split("/")[2])),
                    LocalDate.parse(request.getParameter("startPeriod"), formatter),
                    LocalDate.parse(request.getParameter("endPeriod")));
        }
    }

    /**
     * Переодпределяет метод doPost для обработки post запросов
     * /account - при отправке AccountRequest в формате json добавляет новый счет в базу данных
     *
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AccountRequest accountRequest = gson.fromJson(request.getParameter("accountRequest"),
                AccountRequest.class);

        accountService.create(new Account(accountRequest.getOwnerBankId(), accountRequest.getOwnerUserId(),
                accountRequest.getAmount(), accountRequest.getCreatedAt()));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doPut для обработки put запросов
     * /account - при отправке AccountRequest в формате json обновляет существующий счет в базе данных
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AccountRequest accountRequest = gson.fromJson(request.getParameter("accountRequest"), AccountRequest.class);

        accountService.update(new Account(accountRequest.getOwnerBankId(), accountRequest.getOwnerUserId(),
                accountRequest.getAmount(), accountRequest.getCreatedAt()),
                Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    /**
     * Переодпределяет метод doDelete для обработки delete запросов
     * /account/{id} - удаляет счет с заданным идентификатором
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
            accountService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}

