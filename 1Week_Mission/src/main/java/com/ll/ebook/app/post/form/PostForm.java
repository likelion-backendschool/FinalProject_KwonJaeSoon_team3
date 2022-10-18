package com.ll.ebook.app.post.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PostForm {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
    private String contentHtml;
    private String keywords;
}
