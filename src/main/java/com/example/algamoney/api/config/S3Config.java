package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

//Realiza a configuração do S3 da amazon e cria um bucket caso não exista
@Configuration
public class S3Config {
	
	@Autowired
	private AlgamoneyApiProperty property;
	
	
	@Bean
	public AmazonS3 amazonS3() {
		AWSCredentials credenciais = new BasicAWSCredentials(
			property.getS3().getAccessKeyId(), property.getS3().getSecretKeyAcess());
		
		
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credenciais))
				.withRegion(Regions.US_EAST_1)
				.build();
	
		
		//Cria o Bucket caso não exista, evita criação manual na AWS - é o repositorio
		//Diferença de bucket e simples diretorio:
		//Consigo deixar o arquivo como temporario, e apos a pessoa confirmar, conseguimos deixar como permanente
		if (!amazonS3.doesBucketExistV2(property.getS3().getBucket())) {
			amazonS3.createBucket(
					new CreateBucketRequest(property.getS3().getBucket()));
			
			BucketLifecycleConfiguration.Rule regraExpiracao = 
					new BucketLifecycleConfiguration.Rule()
					.withId("Regra de expiração de arquivos temporários")
					.withFilter(new LifecycleFilter(
							new LifecycleTagPredicate(new Tag("expirar", "true"))))
					.withExpirationInDays(1)
					.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules(regraExpiracao);
			
			amazonS3.setBucketLifecycleConfiguration(property.getS3().getBucket(), 
					configuration);
		} 
		return amazonS3;
	}
}