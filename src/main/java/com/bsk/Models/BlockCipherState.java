package com.bsk.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class BlockCipherState {
    @Getter
    @Setter
    private BlockCipherTypes currentBlockCipherState;
}
