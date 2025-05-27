package tobias.moreno.fin.scope.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.transactions.RequestTransactionDTO;
import tobias.moreno.fin.scope.dto.transactions.ResponseTransactionDTO;
import tobias.moreno.fin.scope.entities.TransactionEntity;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.services.TransactionServiceImpl;
import tobias.moreno.fin.scope.services.interfaces.TransactionService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("transactions")
@RestController
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionServiceImpl transactionService;

	@GetMapping()
	public ResponseEntity<List<ResponseTransactionDTO>> getAll() {
		List<ResponseTransactionDTO> txs = transactionService.findAll();
		return ResponseEntity.ok(txs);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseTransactionDTO> getById(@PathVariable Long id) {
		return transactionService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}


	@PostMapping
	public ResponseEntity<ResponseTransactionDTO> create(@RequestBody RequestTransactionDTO tx) {
		return ResponseEntity.ok(transactionService.save(tx));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponseTransactionDTO> update(
			@PathVariable Long id,
			@RequestBody RequestTransactionDTO tx
	) {
		ResponseTransactionDTO updated = transactionService.update(id, tx);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		transactionService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("asset-type")
	public ResponseEntity<List<ResponseTransactionDTO>> findAllByAssetType(@RequestParam AssetType assetType) {
		List<ResponseTransactionDTO> txs = transactionService.findAllByAssetType(assetType);
		return ResponseEntity.ok(txs);
	}

}
