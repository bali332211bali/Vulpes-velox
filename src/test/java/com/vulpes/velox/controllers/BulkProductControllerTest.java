package com.vulpes.velox.controllers;

import com.vulpes.velox.models.products.BulkProduct;
import com.vulpes.velox.services.bulkproductservice.BulkProductService;
import com.vulpes.velox.services.userservice.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = BulkProductController.class, secure = false)
public class BulkProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BulkProductService bulkProductService;
  @MockBean
  private UserService userService;

  private Map<String, Boolean> errorFlashAttributes;

  @Before
  public void setup() {
    errorFlashAttributes = new HashMap<>();
    errorFlashAttributes.put("bulkProductError", true);
  }

  @Test
  public void bulkProductNewOk() throws Exception {
    when(userService.isUser(isNull())).thenReturn(true);
    when(bulkProductService.getErrorFlashAttributes(
        notNull(), notNull())).thenReturn(Collections.emptyMap());

    mockMvc.perform(post("/bulkProduct/new")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("id", "1")
        .param("name", "Name")
        .param("unitToSet", "Piece")
    )
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/storage/add"))
        .andExpect(view().name("redirect:/storage/add"));

    verify(userService, times(1)).isUser(isNull());
    verifyNoMoreInteractions(userService);

    ArgumentCaptor<BulkProduct> bulkProductArgument = ArgumentCaptor.forClass(BulkProduct.class);
    ArgumentCaptor<String> stringArgument = ArgumentCaptor.forClass(String.class);
    verify(bulkProductService, times(1))
        .saveNewBulkProduct(bulkProductArgument.capture(), stringArgument.capture());
    verify(bulkProductService, times(1))
        .getNewBulkProductFlashAttributes(any(BulkProduct.class), any(RedirectAttributes.class));
    verify(bulkProductService, times(1)).getErrorFlashAttributes(
        any(BulkProduct.class), any(RedirectAttributes.class));
    verifyNoMoreInteractions(bulkProductService);

    BulkProduct bulkProductArgumentValue = bulkProductArgument.getValue();
    assertThat(bulkProductArgumentValue.getId(), is((long) 1));
    assertThat(bulkProductArgumentValue.getName(), is("Name"));
    assertThat(stringArgument.getValue(), is("Piece"));
  }

  @Test
  public void bulkProductNewWithoutName() throws Exception {
    when(userService.isUser(isNull())).thenReturn(true);
    when((Object) bulkProductService.getErrorFlashAttributes(
        notNull(), notNull())).thenReturn(errorFlashAttributes);

    mockMvc.perform(post("/bulkProduct/new")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("id", "1")
        .param("unitToSet", "Piece")
    )
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/storage/add"))
        .andExpect(view().name("redirect:/storage/add"));

    ArgumentCaptor<BulkProduct> bulkProductArgument = ArgumentCaptor.forClass(BulkProduct.class);

    verify(bulkProductService, times(1)).getErrorFlashAttributes(
        bulkProductArgument.capture(), any(RedirectAttributes.class));
    verifyNoMoreInteractions(bulkProductService);

    BulkProduct bulkProductArgumentValue = bulkProductArgument.getValue();
    assertThat(bulkProductArgumentValue.getId(), is((long) 1));
    assertThat(bulkProductArgumentValue.getName(), is(nullValue()));
  }

  @Test
  public void bulkProductNewNotAuthenticated() throws Exception {
    when(userService.isUser(isNull())).thenReturn(false);

    mockMvc.perform(post("/bulkProduct/new")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("id", "1")
        .param("name", "Name")
        .param("unitToSet", "Piece")
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("unauthorized"));

    verify(userService, times(1)).isUser(isNull());
    verify(userService, times(1)).getUserEmail(isNull());
    verifyNoMoreInteractions(userService);
  }

}
