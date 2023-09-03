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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AccountRequest accountRequest = gson.fromJson(request.getParameter("accountRequest"),
                AccountRequest.class);

        accountService.create(new Account(accountRequest.getOwnerBankId(), accountRequest.getOwnerUserId(),
                accountRequest.getAmount(), accountRequest.getCreatedAt()));

        response.setStatus(201);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AccountRequest accountRequest = gson.fromJson(request.getParameter("accountRequest"), AccountRequest.class);

        accountService.update(new Account(accountRequest.getOwnerBankId(), accountRequest.getOwnerUserId(),
                accountRequest.getAmount(), accountRequest.getCreatedAt()),
                Integer.parseInt(request.getPathInfo().split("/")[1]));

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo().split("/").length == 2) {
            accountService.delete(Integer.parseInt(request.getPathInfo().split("/")[1]));
        }

        response.setStatus(200);
    }
}

