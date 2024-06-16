package com.example.onlinetesting.service.RepoFiles;

import com.example.onlinetesting.controller.PersonController;
import com.example.onlinetesting.dto.RepositoryRequest;
import com.example.onlinetesting.enteties.Repo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RepoFilesServiceImpl implements RepoFilesService{
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    public Repo getRepositoryFiles(RepositoryRequest repositoryRequest) {
        Repo repo = new Repo();
        String[] parts = repositoryRequest.getGithubUrl().split("/");
        repo.setOwner(parts[parts.length - 2]);
        repo.setRepo(parts[parts.length - 1]);
        return repo;
    }

    public List<String> getFilesNames(RepositoryRequest repositoryRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> fileNames = new ArrayList<>();
        Repo repo = getRepositoryFiles(repositoryRequest);
        logger.info(repo.getOwner(), repo.getRepo());
        try {
            URL url = new URL("https://api.github.com/repos/" + repo.getOwner() + "/" + repo.getRepo() + "/contents");
            JsonNode rootNode = objectMapper.readTree(url);
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                JsonNode node = elements.next();
                String name = node.get("name").asText();
                fileNames.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
}