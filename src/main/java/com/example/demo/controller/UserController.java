package com.example.demo.controller;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;

import com.example.demo.domain.Mail;
import com.example.demo.domain.Member;
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
	public String login(Model model, @RequestParam(required = false) String error,@RequestParam(required = false) String accountLock,@RequestParam(required = false) String mailAddress, HttpSession session) {
		System.out.println(error);
		if (error != null) {
			model.addAttribute("errorMessage", "wrong email address, password or OTP");
		}
	
		if(accountLock != null) {
			session.setAttribute("mailAddress", mailAddress);
			return "accountLock";
		}
		
		session.removeAttribute("relogin");
		
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
		System.out.println(form);
		Mail mail = new Mail();
		mail.setTitle("RakusItems Register");
		mail.setMailAddress(form.getMailAddress());
		mail.setHtml("finished_register");
		Context context = new Context();
		context.setVariable("uuid", uuid);
		context.setVariable("mailAddress", form.getMailAddress());
		sendMailService.sendMail(context, mail);

		return "redirect:/user";
	}

	/**
	 * 本登録用のview画面に遷移させるメソッドです.
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/registration")
	public String registration(@RequestParam(required = false) String uuid,@RequestParam(required = false) String mailAddress, Model model) {
		if (memberService.findByName(mailAddress) != null) {
			System.err.println("このユーザーは既に登録されています。");
			return "register_error";
		}
		LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime registerDate = memberService.findByProvisionalMember(uuid).getRegisterDate().toLocalDateTime();
		if (Duration.between(registerDate, localDateTime).getSeconds() > 300) {
			System.err.println("有効期限が切れています。");
			memberService.deleteByProvisinalUser(mailAddress);
			model.addAttribute("timeoutError","有効期限が切れています。再度登録してください。");
			return "redirect:register";
		}
		memberService.insertMember(uuid);
		memberService.deleteByProvisinalUser(mailAddress);
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
	public String onetimePasswordRegisterProcess(@Validated MemberForm form, BindingResult rs, HttpSession session,RedirectAttributes attr) {
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
		memberService.updateMember(form, secret);
		// 発行したシークレットキーを削除
		session.removeAttribute("secret");
		session.setAttribute("relogin", "確認のためログアウトしました。再度ログインしてください。");
		
		return "redirect:/user/logout";
	}
	
	/**
	 * アカウントロック解除のメールを送るためのメソッドです.
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/unlockUserSendMail")
	public String unlockUserSendMail(HttpSession session) {
		//メール情報
		Mail mail = new Mail();
		mail.setTitle("RakusItems unlocker");
		mail.setMailAddress(session.getAttribute("mailAddress").toString());
		mail.setHtml("unlock_user");
		//アカウントロック解除キー生成
		Random rnd = new Random();
		int unlockedKey = rnd.nextInt(900001) + 99999;
		
		Member member = new Member();
		member.setMailAddress(session.getAttribute("mailAddress").toString());
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		member.setLockedDate(timeStamp);
		member.setLocked(5);
		member.setUnlockedKey(unlockedKey);
		memberService.updateLocker(member);
		Context context = new Context();
		context.setVariable("mailAddress", session.getAttribute("mailAddress"));
		context.setVariable("unlockedKey", unlockedKey);
		sendMailService.sendMail(context, mail);
		return "redirect:/user";
	}
	
	/**
	 * アカウントロックの解除を認証キーを入力するメソッドです.
	 * 
	 * @param model
	 * @param mailAddress
	 * @return
	 */
	@RequestMapping("unlockUser")
	public String unlockUser(Model model, String mailAddress) {
		model.addAttribute("mailAddress",mailAddress);
		return "unlocked_user";
	}
	
	/**
	 * アカウントロックの解除プロセスを担うメソッドです.
	 * 
	 * @param model
	 * @param session
	 * @param unlockedKey
	 * @param mailAddress
	 * @return
	 */
	@RequestMapping("unlockProcess")
	public String unlockProcess(Model model, HttpSession session, String unlockedKey, String mailAddress) {
		Member member = memberService.findByMailAddress(mailAddress);
		LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime lockedDate = member.getLockedDate().toLocalDateTime();
		if(unlockedKey.isEmpty() || member.getUnlockedKey() != Integer.parseInt(unlockedKey)) {
			model.addAttribute("errorMessage","アカウント解除キーが間違っています");
			return unlockUser(model,mailAddress);
		}else if(Duration.between(lockedDate, localDateTime).getSeconds() > 300) {
			model.addAttribute("errorMessage","アカウント解除キーの有効期限が切れています。再度解除キーを発行してください。");
			return "login";
		}
		
		return "redirect:/user/unlockedUserRegister";
	}
	
	/**
	 * アカウントロックされたユーザーに新しいパスワードを設定するViewを表示させるメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/unlockedUserRegister")
	public String unlockedUserRegister() {
		return "unlockedUserRegister";
	}
	
	/**
	 * アカウントロック解除を行うメソッドです.
	 * 
	 * @param form
	 * @param rs
	 * @return
	 */
	@RequestMapping("unlock")
	public String unlock(@Validated MemberForm form, BindingResult rs) {
		Member member = null;
		if(rs.hasErrors()) {
			return unlockedUserRegister();
		}
		
		if(memberService.findByMailAddress(form.getMailAddress()) != null) {
			member = memberService.findByMailAddress(form.getMailAddress());
		} else {
			rs.rejectValue("mailAddress", "", "登録されていないメールアドレスです。");
			return unlockedUserRegister();
		}
		memberService.updateMember(member, form.getPassword());
		return "redirect:/user/unlockFinished";
	}
	
	/**
	 * アカウントロック解除完了のめそっです.
	 * 
	 * @return
	 */
	@RequestMapping("unlockFinished")
	public String unlockFinished() {
		return "unlock_finished";
	}
}
