package com.example.demo.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

import com.example.demo.domain.LoginMember;
import com.example.demo.form.MemberForm;
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
	public String login(Model model, @RequestParam(required = false) String error, HttpSession session) {
		if (error != null) {
			model.addAttribute("errorMessage", "wrong email address or password");
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
	@RequestMapping("provisionalRegisterProcess")
	public String insertProcess(@Validated MemberForm form, BindingResult rs, Model model) {

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
		memberService.insertProvisionalMember(form, uuid);
		// mailのthymeleaftemplateに仮登録用のuuidをセットする.
		Context context = new Context();
		context.setVariable("uuid", uuid);
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
	public String registration(String uuid) {
		memberService.insertMember(uuid);
		memberService.deleteByProvisinalUser(uuid);
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
	public String userInfo(@AuthenticationPrincipal LoginMember loginMember, Model model) {
		memberService.findByName(loginMember.getUsername());
		model.addAttribute("member",loginMember.getMember());
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
	public String onetimePasswordRegisterProcess(@Validated MemberForm form, BindingResult rs,
			@AuthenticationPrincipal LoginMember loginMember, HttpSession session) {
		//入力値チェック
		if(rs.hasErrors()) {
			return onetimePasswordRegister();
		}
		//ログイン中のユーザー情報が一致しているかの確認
		boolean isUser = form.getMailAddress().equals(loginMember.getUsername());
		boolean isPassword = passwordEncoder.matches(form.getPassword(), loginMember.getMember().getPassword());
		//入力されたワンタイムパスワードがQRコードに含まれたシークレットキーを格納したセッションと一致するかの確認
		int verificationCode = Integer.parseInt(form.getOnetimePassword());
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		String secret = session.getAttribute("secret").toString();
		boolean isCodeValid = gAuth.authorize(secret, verificationCode);
		//一致しなければフォームに戻す
		if (!isCodeValid || !isPassword || !isUser) {
			System.err.println("どれかの入力値が間違っています。");
			return onetimePasswordRegister();
		}
		
		memberService.updateMember(form,secret,loginMember.getUsername());
		//発行したシークレットキーを削除
		session.removeAttribute("secret");

		return "redirect:info";
	}

}
