package com.example.demo.service;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.example.demo.domain.Mail;

@Service
public class SendMailService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Async
	public void sendMail(Context context,Mail mail) {

		javaMailSender.send(new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
				helper.setFrom("side19river951happy128@gmail.com");
				helper.setTo(mail.getMailAddress());
				helper.setSubject(mail.getTitle());
				helper.setText(getMailBody(mail.getHtml(), context),true);
			}
		});
	}

		private String getMailBody(String templateName, Context context) {
			SpringTemplateEngine templateEngine = new SpringTemplateEngine();
			templateEngine.setTemplateResolver(mailTemplateResolver());
			return templateEngine.process(templateName, context);
		}

		private ClassLoaderTemplateResolver mailTemplateResolver() {
			ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
			templateResolver.setTemplateMode(TemplateMode.HTML);
			templateResolver.setPrefix("mailtemplate/");
			templateResolver.setSuffix(".html");
			templateResolver.setCharacterEncoding("UTF-8");
			templateResolver.setCacheable(true);
			return templateResolver;
		}
	}
