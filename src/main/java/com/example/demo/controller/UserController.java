package com.example.demo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

import com.example.demo.form.MemberForm;
import com.example.demo.security.PrincipalGetter;
import com.example.demo.service.MemberService;
import com.example.demo.service.SendMailService;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Controller
@EnableAsync
@RequestMapping("/user")
public class UserController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private PrincipalGetter principalGetter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	private MemberForm setUpMemberForm() {
		return new MemberForm();
	}

	/**
	 * ログインページのViewメソッドです.
	 * 
	 * @return ログインページのView
	 */
	@RequestMapping()
	public String login(Model model, @RequestParam(required = false) String error) {
		if (error != null) {
			model.addAttribute("errorMessage", "wrong email address, password or OTP");
		}
		return "login";
	}

	/**
	 * ユーザー登録画面のViewメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/provisionalRegister")
	public String insert() {
		return "register";
	}

	/**
	 * ユーザー登録処理です.
	 * 
	 * @return
	 */
	@RequestMapping("/provisionalRegisterProcess")
	public String insertProcess(@Validated MemberForm form, BindingResult rs, Model model, HttpSession session) {

		if (!(form.getIsPassCheck().test(true))) {
			rs.rejectValue("password", "", "Password and confirmation password must be the same");
			return insert();
		}
		if (memberService.findByName(form.getMailAddress()) != null) {
			rs.rejectValue("mailAddress", "", "The emailaddress is already registered");
			return insert();
		}
		if (rs.hasErrors()) {
			return insert();
		}
		// 仮登録用のuuidを生成
		String uuid = UUID.randomUUID().toString();
		String secret = null;
		if(!form.getOnetimePassword().equals("000000")) {
			int verificationCode = Integer.parseInt(form.getOnetimePassword());
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			secret = session.getAttribute("secret").toString();
			boolean isCodeValid = gAuth.authorize(secret, verificationCode);
			if (!isCodeValid) {
				System.err.println("どれかの入力値が間違っています。");
				return insert();
			}
		}
		memberService.insertProvisionalMember(form, secret, uuid);
		// mailのthymeleaftemplateに仮登録用のuuidをセットする.
		Context context = new Context();
		context.setVariable("uuid", uuid);
		context.setVariable("mailAddress", form.getMailAddress());
		sendMailService.sendMail(context, form.getMailAddress());

		return "redirect:/user";
	}

	/**
	 * 本登録用のview画面に遷移させるメソッドです.
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/registration")
	public String registration(String uuid, String mailAddress, Model model) {
		if (memberService.findByName(mailAddress) != null) {
			System.err.println("このユーザーは既に登録されています。");
			return "register_error";
		}
		LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime registerDate = memberService.findByProvisionalMember(uuid).getRegisterDate().toLocalDateTime();
		if (Duration.between(registerDate, localDateTime).getSeconds() > 300) {
			System.err.println("有効期限が切れています。");
			memberService.deleteByProvisinalUser(uuid);
			model.addAttribute("timeoutError","有効期限が切れています。再度登録してください。");
			return "redirect:register";
		}
		memberService.insertMember(uuid);
		return "redirect:finished";
	}

	/**
	 * 本登録完了のメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("finished")
	public String finished() {
		return "finished";
	}

	/**
	 * ユーザー情報を表示させるメソッドです.
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/info")
	public String userInfo(Model model) {
		model.addAttribute("member", principalGetter.getMember());
		return "user_info";
	}

	/**
	 * ワンタイムパスワードのQRコードを表示するViewメソッドです.
	 * 
	 * @param loginMember
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping("/onetimePassword")
	public String ontimePasswordRegister() {
		return "onetimePasswordGenerator";
	}

	/**
	 * 既にログインしているユーザーのワンタイムパスワードを登録するViewを表示するメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/onetimePasswordRegister")
	public String onetimePasswordRegister() {
		return "onetimePasswordRegister";
	}

	/**
	 * @param form
	 * @param rs
	 * @param loginMember
	 * @param session
	 * @return
	 */
	@RequestMapping("/onetimePasswordRegisterProcess")
	public String onetimePasswordRegisterProcess(@Validated MemberForm form, BindingResult rs, HttpSession session) {
		// 入力値チェック
		if (rs.hasErrors()) {
			return onetimePasswordRegister();
		}
		// ログイン中のユーザー情報が一致しているかの確認
		boolean isUser = form.getMailAddress().equals(principalGetter.getMember().getMailAddress());
		boolean isPassword = passwordEncoder.matches(form.getPassword(), principalGetter.getMember().getPassword());
		// 入力されたワンタイムパスワードがQRコードに含まれたシークレットキーを格納したセッションと一致するかの確認
		int verificationCode = Integer.parseInt(form.getOnetimePassword());
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		String secret = session.getAttribute("secret").toString();
		boolean isCodeValid = gAuth.authorize(secret, verificationCode);
		// 一致しなければフォームに戻す
		if (!isCodeValid || !isPassword || !isUser) {
			System.err.println("どれかの入力値が間違っています。");
			return onetimePasswordRegister();
		}
		memberService.updateMember(form, secret, principalGetter.getMember().getMailAddress());
		// 発行したシークレットキーを削除
		session.removeAttribute("secret");

		return "redirect:info";
	}

}
