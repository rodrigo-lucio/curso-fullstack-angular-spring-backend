package com.example.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

//Faz o upload de arquivo para o S3 da amazon
@Component
public class S3 {

	private static final Logger logger = LoggerFactory.getLogger(S3.class);

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;

	public String salvarTemporariamente(MultipartFile arquivo) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());

		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(algamoneyApiProperty.getS3().getBucket(),
					nomeUnico, arquivo.getInputStream(), objectMetadata).withAccessControlList(acl);

			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));

			amazonS3.putObject(putObjectRequest);

			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
			}

			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}

	public String configurarUrl(String objeto) {

		return "\\\\" + algamoneyApiProperty.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}

	//Deixa o arquivo permanente, remove as tags de temporaria 
	public void salvar(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
			algamoneyApiProperty.getS3().getBucket(), 
			objeto, 
			new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}

	//Remove o arquivo do bucket
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(algamoneyApiProperty.getS3().getBucket(), objeto);
		amazonS3.deleteObject(deleteObjectRequest);
	}
	
	//Substitui o anexo
	public void substituir(String objetoAntigo, String objetoNovo) {
		if(StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		
		salvar(objetoNovo);
	}
	
	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + " " + originalFilename;
	}

	



}
