package com.example.onlinetesting.service.RepoFiles;

import com.example.onlinetesting.dto.RepositoryRequest;
import com.example.onlinetesting.enteties.Repo;

import java.util.List;

public interface RepoFilesService {
    Repo getRepositoryFiles(RepositoryRequest repositoryRequest);
    List<String> getFilesNames(RepositoryRequest repositoryRequest);
}
