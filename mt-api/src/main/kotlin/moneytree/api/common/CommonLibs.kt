package moneytree.api.common

import org.http4k.lens.Path
import org.http4k.lens.int

val intLens = Path.int().of("id")
