package cms.sre.product_list_emitter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product-list-emitter")
public class EmitterController {
    @RequestMapping(method = RequestMethod.GET)
    public String get(){ return "{status: 'ok'}"; }
}
