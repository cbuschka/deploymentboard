package com.github.cbuschka.poboard.domain.scm;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CodeRepository
{
	private List<Change> changes;
}
